/**
 * Copyright 2011-2012 eBusiness Information, Groupe Excilys (www.excilys.com)
 * Copyright 2012 Gilt Groupe, Inc. (www.gilt.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.gatling.http.action.ws

import com.ning.http.client.websocket.WebSocket

import akka.actor.ActorRef
import io.gatling.core.action.{ Action, Chainable, Failable }
import io.gatling.core.result.message.KO
import io.gatling.core.result.writer.{ DataWriter, RequestMessage }
import io.gatling.core.session.{ Expression, Session }
import io.gatling.core.util.TimeHelper.nowMillis
import io.gatling.core.validation.{ Failure, Success, SuccessWrapper }
import io.gatling.http.config.HttpProtocol

class CloseWebSocketAction(requestName: Expression[String], wsName: String, val next: ActorRef, protocol: HttpProtocol) extends Action with Chainable with Failable {

	def executeOrFail(session: Session) = {

		def close(requestName: String) = {
			logger.info(s"Closing websocket '$wsName': Scenario '${session.scenarioName}', UserId #${session.userId}")
			session(wsName).validate[(ActorRef, WebSocket)] match {
				case Success((wsActor, _)) =>
					wsActor ! Close(requestName, next, session)
					session.success

				case f @ Failure(message) =>
					val now = nowMillis
					DataWriter.tell(RequestMessage(
						session.scenarioName,
						session.userId,
						Nil,
						requestName,
						now,
						now,
						now,
						now,
						KO,
						Some(message),
						Nil))
					f
			}
		}

		for {
			requestName <- requestName(session)
			outcome <- close(requestName)
		} yield outcome
	}
}
