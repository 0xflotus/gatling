/**
 * Copyright 2011-2014 eBusiness Information, Groupe Excilys (www.ebusinessinformation.fr)
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
package io.gatling.http.util

import java.net.{ URI, URLDecoder }

import scala.collection.breakOut
import scala.io.Codec.UTF8

import com.ning.http.client.{ FluentCaseInsensitiveStringsMap, ProxyServer, Realm }
import com.ning.http.client.Realm.AuthScheme
import com.ning.http.util.AsyncHttpProviderUtils
import com.typesafe.scalalogging.slf4j.StrictLogging

import io.gatling.core.config.Credentials
import io.gatling.core.session.{ Expression, Session }
import io.gatling.http.{ HeaderNames, HeaderValues }

object HttpHelper extends StrictLogging {

	val httpScheme = "http"
	val httpsScheme = "https"
	val wsScheme = "ws"
	val wssScheme = "wss"

	def parseFormBody(body: String): List[(String, String)] = {
		def utf8Decode(s: String) = URLDecoder.decode(s, UTF8.name)

		body
			.split("&")
			.map(_.split("=", 2))
			.map { pair =>
				val paramName = utf8Decode(pair(0))
				val paramValue = if (pair.length > 1) utf8Decode(pair(1)) else ""
				paramName -> paramValue
			}(breakOut)
	}

	def buildRealm(username: Expression[String], password: Expression[String]): Expression[Realm] = (session: Session) =>
		for {
			usernameValue <- username(session)
			passwordValue <- password(session)
		} yield buildRealm(usernameValue, passwordValue)

	def buildRealm(username: String, password: String): Realm = new Realm.RealmBuilder().setPrincipal(username).setPassword(password).setUsePreemptiveAuth(true).setScheme(AuthScheme.BASIC).build

	def isCss(headers: FluentCaseInsensitiveStringsMap) = Option(headers.getFirstValue(HeaderNames.CONTENT_TYPE)).exists(_.contains(HeaderValues.TEXT_CSS))
	def isHtml(headers: FluentCaseInsensitiveStringsMap) = Option(headers.getFirstValue(HeaderNames.CONTENT_TYPE)).exists(ct => ct.contains(HeaderValues.TEXT_HTML) || ct.contains(HeaderValues.APPLICATION_XHTML))
	def resolveFromURI(rootURI: URI, relative: String) = AsyncHttpProviderUtils.getRedirectUri(rootURI, relative)
	def resolveFromURISilently(rootURI: URI, relative: String): Option[URI] =
		try {
			Some(resolveFromURI(rootURI, relative))
		} catch {
			case e: Exception =>
				logger.error("Failed to resolve URI", e)
				None
		}

	val redirectStatusCodes = Vector(301, 302, 303, 307, 308)
	def isRedirect(statusCode: Int) = redirectStatusCodes.contains(statusCode)
	def isNotModified(statusCode: Int) = statusCode == 304

	def isSecure(uri: URI) = uri.getScheme == httpsScheme || uri.getScheme == wssScheme

	def isAbsoluteHttpUrl(url: String) = url.startsWith(httpScheme)
	def isAbsoluteWsUrl(url: String) = url.startsWith(wsScheme)
}

