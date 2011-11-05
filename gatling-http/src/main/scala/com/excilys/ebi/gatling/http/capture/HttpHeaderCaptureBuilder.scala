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
package com.excilys.ebi.gatling.http.capture.capturer

import com.excilys.ebi.gatling.core.context.Context
import com.excilys.ebi.gatling.core.util.StringHelper.interpolateString
import com.excilys.ebi.gatling.http.request.HttpPhase.HttpPhase
import com.excilys.ebi.gatling.http.capture.HttpHeaderCapture
import com.excilys.ebi.gatling.http.capture.HttpCapture
import com.excilys.ebi.gatling.http.capture.HttpCaptureBuilder

object HttpHeaderCaptureBuilder {
	def header(expressionFormatter: Context => String) = new HttpHeaderCaptureBuilder(Some(expressionFormatter), None, None)
	def header(expression: String): HttpHeaderCaptureBuilder = header((c: Context) => expression)
	def header(expressionToFormat: String, interpolations: String*): HttpHeaderCaptureBuilder = header((c: Context) => interpolateString(c, expressionToFormat, interpolations))
}

class HttpHeaderCaptureBuilder(expressionFormatter: Option[Context => String], attribute: Option[String], httpPhase: Option[HttpPhase])
		extends HttpCaptureBuilder[HttpHeaderCaptureBuilder](expressionFormatter, attribute, httpPhase) {

	def newInstance(expressionFormatter: Option[Context => String], attribute: Option[String], httpPhase: Option[HttpPhase]) = {
		new HttpHeaderCaptureBuilder(expressionFormatter, attribute, httpPhase)
	}

	def build: HttpCapture = new HttpHeaderCapture(expressionFormatter.get, attribute.get)
}