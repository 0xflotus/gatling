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

import com.excilys.ebi.gatling.core.log.Logging
import com.excilys.ebi.gatling.core.session.Session
import com.excilys.ebi.gatling.core.util.ClassSimpleNameToString

import akka.actor.Actor

/**
 * This trait represents an Action in Gatling terms.
 *
 * An action can be executed in the scenario via the exec command
 */
trait Action extends Actor with Logging with ClassSimpleNameToString {

	def receive = {
		case s: Session => execute(s)
		case _ => throw new IllegalArgumentException("Unknown message type")
	}

	/**
	 * This method is used to send a message to this actor
	 *
	 * @param session The session of the scenario
	 * @return Nothing
	 */
	def execute(session: Session)

	/**
	 * This is the Uuid of the current actor
	 *
	 * @return a string containing the Uuid of the actor
	 */
	lazy val uuidAsString = self.uuid.toString
}