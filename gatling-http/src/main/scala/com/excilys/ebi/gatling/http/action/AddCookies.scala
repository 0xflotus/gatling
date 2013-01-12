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
package com.excilys.ebi.gatling.http.action

import java.net.URI

import com.excilys.ebi.gatling.core.action.Bypassable
import com.excilys.ebi.gatling.core.session.{ Expression, Session }
import com.excilys.ebi.gatling.http.cookie.CookieHandling
import com.ning.http.client.{ Cookie => AHCCookie }

import akka.actor.ActorRef
import scalaz.{ Failure, Success }

class AddCookies(url: Expression[String], cookies: Expression[List[AHCCookie]], val next: ActorRef) extends Bypassable {

	def execute(session: Session) {

		val resolvedUrl = url(session)
		val resolvedCookies = cookies(session)

		val newSession = for {
			url <- url(session)
			cookies <- cookies(session)
		} yield CookieHandling.storeCookies(session, URI.create(url), cookies)

		newSession match {
			case Success(newSession) => next ! newSession
			case Failure(message) => error("Could not build cookie: " + message)
		}
	}
}