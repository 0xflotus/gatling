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
package com.excilys.ebi.gatling.http.cookie
import java.net.URI

import com.excilys.ebi.gatling.core.session.Session.GATLING_PRIVATE_ATTRIBUTE_PREFIX
import com.excilys.ebi.gatling.core.session.Session
import com.ning.http.client.Cookie



case class CookieKey(domain: String, path: String, name: String)

trait CookieHandling {

	val COOKIES_CONTEXT_KEY = GATLING_PRIVATE_ATTRIBUTE_PREFIX + "http.cookies"

	def getStoredCookies(session: Session, url: String): List[Cookie] = {
		session.getAttributeAsOption[Map[CookieKey, Cookie]](COOKIES_CONTEXT_KEY) match {
			case Some(storedCookies) if (!storedCookies.isEmpty) => {
				val uri = URI.create(url)
				val uriHost = uri.getHost
				val uriPath = uri.getPath
				storedCookies
					.filter { case (key, _) => uriHost.endsWith(key.domain) && uriPath.startsWith(key.path) }
					.map { case (_, cookie) => cookie }
					.toList
			}
			case _ => Nil
		}
	}

	def storeCookies(session: Session, url: String, cookies: Seq[Cookie]) = {

		def newCookieKey(cookie: Cookie, uriHost: String, uriPath: String) = {
			val cookieDomain = Option(cookie.getDomain).getOrElse(uriHost)
			val cookiePath = Option(cookie.getPath).getOrElse(uriPath)
			CookieKey(cookieDomain, cookiePath, cookie.getName)
		}

		if (!cookies.isEmpty) {
			val storedCookies: Map[CookieKey, Cookie] = session.getAttributeAsOption(COOKIES_CONTEXT_KEY).getOrElse(Map.empty[CookieKey, Cookie])

			val uri = URI.create(url)
			val uriHost = uri.getHost
			val uriPath = uri.getPath

			val (deletedCookies, nonDeletedCookies) = cookies.partition(_.getValue == "deleted")

			val deletedCookieKeys = deletedCookies.map(newCookieKey(_, uriHost, uriPath))
			val nonDeletedStoredCookies = storedCookies.filterKeys(!deletedCookieKeys.contains(_))

			val newCookies = nonDeletedStoredCookies ++ nonDeletedCookies.map { cookie => newCookieKey(cookie, uriHost, uriPath) -> cookie }

			session.setAttribute(COOKIES_CONTEXT_KEY, newCookies)
		} else
			session
	}
}
