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
package com.excilys.ebi.gatling.http.capture.body.check

import com.excilys.ebi.gatling.core.context.Context
import com.excilys.ebi.gatling.http.request.HttpPhase._
import com.excilys.ebi.gatling.core.util.StringHelper._
import com.excilys.ebi.gatling.core.capture.check.NonEqualityCheckType
import com.excilys.ebi.gatling.core.capture.check.ExistenceCheckType
import com.excilys.ebi.gatling.core.capture.check.CheckType
import com.excilys.ebi.gatling.core.capture.check.EqualityCheckType
import com.excilys.ebi.gatling.core.capture.check.NonExistenceCheckType
import com.excilys.ebi.gatling.http.capture.HttpCheckBuilder

object HttpBodyXPathCheckBuilder {

	def xpathEquals(what: Context => String, expected: String) = new HttpBodyXPathCheckBuilder(Some(what), Some(EMPTY), Some(CompletePageReceived), Some(EqualityCheckType), Some(expected))
	def xpathEquals(expression: String, expected: String): HttpBodyXPathCheckBuilder = xpathEquals((c: Context) => expression, expected)

	def xpathNotEquals(what: Context => String, expected: String) = new HttpBodyXPathCheckBuilder(Some(what), Some(EMPTY), Some(CompletePageReceived), Some(NonEqualityCheckType), Some(expected))
	def xpathNotEquals(expression: String, expected: String): HttpBodyXPathCheckBuilder = xpathNotEquals((c: Context) => expression, expected)

	def xpathExists(what: Context => String) = new HttpBodyXPathCheckBuilder(Some(what), Some(EMPTY), Some(CompletePageReceived), Some(ExistenceCheckType), None)
	def xpathExists(expression: String): HttpBodyXPathCheckBuilder = xpathExists((c: Context) => expression)

	def xpathNotExists(what: Context => String) = new HttpBodyXPathCheckBuilder(Some(what), Some(EMPTY), Some(CompletePageReceived), Some(NonExistenceCheckType), None)
	def xpathNotExists(expression: String): HttpBodyXPathCheckBuilder = xpathNotExists((c: Context) => expression)
}

class HttpBodyXPathCheckBuilder(what: Option[Context => String], to: Option[String], when: Option[HttpPhase], checkType: Option[CheckType], expected: Option[String])
		extends HttpCheckBuilder[HttpBodyXPathCheckBuilder](what, to, when, checkType, expected) {

	def newInstance(what: Option[Context => String], to: Option[String], when: Option[HttpPhase], checkType: Option[CheckType], expected: Option[String]) = {
		new HttpBodyXPathCheckBuilder(what, to, when, checkType, expected)
	}

	def build = new HttpBodyXPathCheck(what.get, to.get, when.get, checkType.get, expected.get)
}
