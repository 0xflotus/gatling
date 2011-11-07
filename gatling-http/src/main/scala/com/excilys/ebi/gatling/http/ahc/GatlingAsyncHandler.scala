/**
 * Copyright 2011 eBusiness Information, Groupe Excilys (www.excilys.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.excilys.ebi.gatling.http.ahc

import java.lang.Void
import java.util.concurrent.TimeUnit

import scala.collection.immutable.HashMap
import scala.collection.mutable.{ Set => MSet }
import scala.collection.mutable.{ HashMap => MHashMap }
import scala.collection.mutable.MultiMap

import org.jboss.netty.handler.codec.http.HttpHeaders.Names.SET_COOKIE
import org.joda.time.DateTime

import com.excilys.ebi.gatling.core.action.Action
import com.excilys.ebi.gatling.core.check.extractor.Extractor
import com.excilys.ebi.gatling.core.check.extractor.ExtractorFactory
import com.excilys.ebi.gatling.core.context.Context
import com.excilys.ebi.gatling.core.log.Logging
import com.excilys.ebi.gatling.core.result.message.ResultStatus.KO
import com.excilys.ebi.gatling.core.result.message.ResultStatus.OK
import com.excilys.ebi.gatling.core.result.message.ResultStatus.ResultStatus
import com.excilys.ebi.gatling.core.result.message.ActionInfo
import com.excilys.ebi.gatling.http.check.HttpCheck
import com.excilys.ebi.gatling.http.request.HttpPhase.CompletePageReceived
import com.excilys.ebi.gatling.http.request.HttpPhase.HttpPhase
import com.excilys.ebi.gatling.http.request.HttpPhase
import com.excilys.ebi.gatling.http.util.GatlingHttpHelper.COOKIES_CONTEXT_KEY
import com.ning.http.client.AsyncHandler.STATE
import com.ning.http.client.Response.ResponseBuilder
import com.ning.http.client.AsyncHandler
import com.ning.http.client.Cookie
import com.ning.http.client.HttpResponseBodyPart
import com.ning.http.client.HttpResponseHeaders
import com.ning.http.client.HttpResponseStatus
import com.ning.http.client.Response
import com.ning.http.util.AsyncHttpProviderUtils.parseCookie

import akka.actor.Actor.registry.actorFor

class GatlingAsyncHandler(context: Context, checks: MSet[HttpCheck], next: Action, requestName: String, groups: List[String])
		extends AsyncHandler[Void] with Logging {

	private val executionStartTimeNano = System.nanoTime

	private val executionStartDate = DateTime.now()

	private val indexedChecks: MultiMap[HttpPhase, HttpCheck] = new MHashMap[HttpPhase, MSet[HttpCheck]] with MultiMap[HttpPhase, HttpCheck]
	checks.foreach(check => indexedChecks.addBinding(check.when, check))

	private val identifier = requestName + context.getUserId

	private val responseBuilder = new ResponseBuilder()

	def onStatusReceived(responseStatus: HttpResponseStatus): STATE = {
		responseBuilder.accumulate(responseStatus)
		STATE.CONTINUE
	}

	def onHeadersReceived(headers: HttpResponseHeaders): STATE = {

		def handleCookies(headers: HttpResponseHeaders) {
			val headersMap = headers.getHeaders

			val setCookieHeaders = headersMap.get(SET_COOKIE)
			if (setCookieHeaders != null) {
				var contextCookies = context.getAttributeAsOption(COOKIES_CONTEXT_KEY).getOrElse(HashMap.empty).asInstanceOf[HashMap[String, Cookie]]

				val it = setCookieHeaders.iterator
				while (it.hasNext) {
					val cookie = parseCookie(it.next)
					contextCookies += (cookie.getName -> cookie)
				}

				logger.debug("Cookies put in Context: {}", contextCookies)

				context setAttribute (COOKIES_CONTEXT_KEY, contextCookies)
			}
		}

		responseBuilder.accumulate(headers)
		handleCookies(headers)

		STATE.CONTINUE
	}

	def onBodyPartReceived(bodyPart: HttpResponseBodyPart): STATE = {
		// only store bodyparts if they are to be analyzed
		if (isPhaseToBeProcessed(CompletePageReceived)) {
			responseBuilder.accumulate(bodyPart)
		}
		STATE.CONTINUE
	}

	def onCompleted(): Void = {
		val response = responseBuilder.build
		processResponse(response)
		null
	}

	def onThrowable(throwable: Throwable) = {
		logger.error("{}\n{}", throwable.getClass, throwable.getStackTraceString)
		sendLogAndExecuteNext(KO, throwable.getMessage, System.nanoTime)
	}

	private def sendLogAndExecuteNext(requestResult: ResultStatus, requestMessage: String, processingStartTimeNano: Long) = {
		actorFor(context.getWriteActorUuid).map { a =>
			val responseTimeMillis = TimeUnit.MILLISECONDS.convert(processingStartTimeNano - executionStartTimeNano, TimeUnit.NANOSECONDS)
			a ! ActionInfo(context.getScenarioName, context.getUserId, "Request " + requestName, executionStartDate, responseTimeMillis, requestResult, requestMessage, groups)
		}

		context.setAttribute(Context.LAST_ACTION_DURATION_KEY, System.nanoTime() - processingStartTimeNano)

		logger.debug("Context Cookies sent to next action: {}", context.getAttributeAsOption(COOKIES_CONTEXT_KEY).getOrElse(HashMap.empty))
		next.execute(context)
	}

	private def isPhaseToBeProcessed(httpPhase: HttpPhase) = indexedChecks.get(httpPhase).isDefined

	private def processResponse(response: Response) {

		def prepareExtractors(checks: MSet[HttpCheck], response: Response): MHashMap[ExtractorFactory[Response], Extractor] = {

			val extractors: MHashMap[ExtractorFactory[Response], Extractor] = MHashMap.empty
			checks.foreach { check =>
				val extractorFactory = check.how
				if (extractors.get(extractorFactory).isEmpty)
					extractors += extractorFactory -> extractorFactory.getExtractor(response)
			}

			extractors
		}

		val processingStartTimeNano = System.nanoTime

		HttpPhase.values.foreach { httpPhase =>

			if (isPhaseToBeProcessed(httpPhase)) {
				val phaseChecks = indexedChecks.get(httpPhase).get

				val phaseExtractors = prepareExtractors(phaseChecks, response)

				for (check <- phaseChecks) {
					check match {
						case c: HttpCheck =>
							val extractor = phaseExtractors.get(check.how).get
							val value = extractor.extract(c.what.apply(context))
							logger.debug("Captured Value: {}", value)

							if (c.isInstanceOf[HttpCheck] && !c.asInstanceOf[HttpCheck].getResult(value)) {
								logger.warn("CHECK RESULT: false expected {} but received {}", c, value)
								sendLogAndExecuteNext(KO, c + " failed", processingStartTimeNano)
								return

							} else if (!value.isDefined) {
								logger.warn("Capture {} could not get value required by user", c)
								sendLogAndExecuteNext(KO, c + " failed", processingStartTimeNano)
								return

							} else if (c.to.isDefined) {
								context.setAttribute(c.to.get, value.get.toString)
							}

						case _ => throw new IllegalArgumentException
					}
				}
			}
		}

		sendLogAndExecuteNext(OK, "Request Executed Successfully", processingStartTimeNano)
	}
}