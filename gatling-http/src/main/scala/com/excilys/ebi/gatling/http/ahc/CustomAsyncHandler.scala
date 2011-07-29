package com.excilys.ebi.gatling.http.ahc

import scala.collection.mutable.{ HashSet, MultiMap }

import com.ning.http.client.AsyncHandler
import com.ning.http.client.AsyncHandler.STATE
import com.ning.http.client.Response
import com.ning.http.client.Response.ResponseBuilder
import com.ning.http.client.HttpResponseStatus
import com.ning.http.client.HttpResponseHeaders
import com.ning.http.client.HttpResponseBodyPart

import com.excilys.ebi.gatling.core.action.Action
import com.excilys.ebi.gatling.core.log.Logging
import com.excilys.ebi.gatling.core.result.message.ActionInfo
import com.excilys.ebi.gatling.core.context.builder.ContextBuilder.makeContext
import com.excilys.ebi.gatling.core.context.Context

import com.excilys.ebi.gatling.http.phase.HttpPhase
import com.excilys.ebi.gatling.http.processor.HttpProcessor
import com.excilys.ebi.gatling.http.processor.capture.HttpCapture
import com.excilys.ebi.gatling.http.processor.assertion.HttpAssertion
import com.excilys.ebi.gatling.http.phase._
import com.excilys.ebi.gatling.http.request.HttpRequest

import java.util.Date
import java.util.concurrent.TimeUnit

import akka.actor.Actor.registry.actorFor

class CustomAsyncHandler(context: Context, assertions: MultiMap[HttpPhase, HttpAssertion], captures: MultiMap[HttpPhase, HttpCapture], next: Action, executionStartTime: Long, executionStartDate: Date,
                         request: HttpRequest)
    extends AsyncHandler[Response] with Logging {

  private val responseBuilder: ResponseBuilder = new ResponseBuilder()

  var contextBuilder = makeContext fromContext context

  private def processResponse(httpPhase: HttpPhase, placeToSearch: Any): STATE = {
    for (a <- assertions.get(httpPhase).getOrElse(new HashSet)) {
      // Do assert Stuff
    }

    for (c <- captures.get(httpPhase).getOrElse(new HashSet)) {
      val value = c.capture(placeToSearch)
      logger.info("Captured Value: {}", value)
      contextBuilder = contextBuilder setAttribute (c.getAttrKey, value.getOrElse(throw new Exception("Capture string not found")).toString)
    }
    STATE.CONTINUE
  }

  def onStatusReceived(responseStatus: HttpResponseStatus): STATE = {
    responseBuilder.accumulate(responseStatus)
    processResponse(new StatusReceived, responseStatus.getStatusCode)
  }

  def onHeadersReceived(headers: HttpResponseHeaders): STATE = {
    responseBuilder.accumulate(headers)
    processResponse(new HeadersReceived, headers.getHeaders) // Process headers is not that simple.
  }

  def onBodyPartReceived(bodyPart: HttpResponseBodyPart): STATE = {
    responseBuilder.accumulate(bodyPart)
    STATE.CONTINUE
  }

  def onCompleted(): Response = {
    logger.debug("Response Received for request: {}", request.getRequest.getUrl)
    val processingStartTime: Long = System.nanoTime()
    val response = responseBuilder.build
    processResponse(new CompletePageReceived, response)

    actorFor(context.getWriteActorUuid).map { a =>
      a ! ActionInfo(context.getUserId, "Request " + request.getName, executionStartDate, TimeUnit.MILLISECONDS.convert(System.nanoTime - executionStartTime, TimeUnit.NANOSECONDS), "OK", "Request Executed Successfully")
    }

    next.execute(contextBuilder setElapsedActionTime (System.nanoTime() - processingStartTime) setCookies response.getCookies build)
    null
  }

  def onThrowable(throwable: Throwable) = {
    throwable.printStackTrace
  }

}