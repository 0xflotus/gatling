/**
 * Copyright 2011-2012 eBusiness Information, Groupe Excilys (www.excilys.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * 		http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.excilys.ebi.gatling.core.action

import com.excilys.ebi.gatling.core.session.handler.{TimerBasedIterationHandler, CounterBasedIterationHandler}
import com.excilys.ebi.gatling.core.session.Session

import akka.actor.scala2ActorRef
import akka.actor.ActorRef

/**
 * Represents a While in the scenario.
 *
 * @constructor creates a While loop in the scenario
 * @param testFunction the function that will be used to decide when to stop the loop
 * @param loopNext the chain executed if testFunction evaluates to true, passed as a Function for construct time
 * @param next the chain executed if testFunction evaluates to false
 * @param counterName the name of the counter for this loop
 */
class WhileAction(testFunction: (Session, Action) => Boolean, loopNext: ActorRef => ActorRef, next: ActorRef, counterName: Option[String])
		extends Action with TimerBasedIterationHandler with CounterBasedIterationHandler {

	val loopNextAction = loopNext(self)

	/**
	 * Evaluates the testFunction and if true executes the first action of loopNext
	 * else it executes the first action of next
	 *
	 * @param session Session of the scenario
	 * @return Nothing
	 */
	def execute(session: Session) = {
		val uuid = self.uuid.toString

		var newSession = init(session, uuid, counterName)

		newSession = increment(newSession, uuid, counterName)

		if (testFunction(newSession, this)) {
			loopNextAction ! newSession
		} else {
			newSession = expire(newSession, uuid, counterName)
			next ! newSession
		}
	}
}