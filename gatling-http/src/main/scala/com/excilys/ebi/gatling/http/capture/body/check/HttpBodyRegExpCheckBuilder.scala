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
import com.excilys.ebi.gatling.http.capture.HttpCheck
import com.excilys.ebi.gatling.http.capture.HttpCheckBuilder

object HttpBodyRegExpCheckBuilder {
	def regexpEquals(what: Context => String, expected: String) = new HttpBodyRegExpCheckBuilder(Some(what), Some(EMPTY), Some(CompletePageReceived), Some(EqualityCheckType), Some(expected))
	def regexpEquals(expression: String, expected: String): HttpBodyRegExpCheckBuilder = regexpEquals((c: Context) => expression, expected)

	def regexpNotEquals(what: Context => String, expected: String) = new HttpBodyRegExpCheckBuilder(Some(what), Some(EMPTY), Some(CompletePageReceived), Some(NonEqualityCheckType), Some(expected))
	def regexpNotEquals(expression: String, expected: String): HttpBodyRegExpCheckBuilder = regexpNotEquals((c: Context) => expression, expected)

	def regexpExists(what: Context => String) = new HttpBodyRegExpCheckBuilder(Some(what), Some(EMPTY), Some(CompletePageReceived), Some(ExistenceCheckType), Some(EMPTY))
	def regexpExists(expression: String): HttpBodyRegExpCheckBuilder = regexpExists((c: Context) => expression)

	def regexpNotExists(what: Context => String) = new HttpBodyRegExpCheckBuilder(Some(what), Some(EMPTY), Some(CompletePageReceived), Some(NonExistenceCheckType), Some(EMPTY))
	def regexpNotExists(expression: String): HttpBodyRegExpCheckBuilder = regexpNotExists((c: Context) => expression)
}

class HttpBodyRegExpCheckBuilder(what: Option[Context => String], to: Option[String], when: Option[HttpPhase], checkType: Option[CheckType], expected: Option[String])
		extends HttpCheckBuilder[HttpBodyRegExpCheckBuilder](what, to, when, checkType, expected) {

	// FIXME remove
	def newInstance(what: Option[Context => String], to: Option[String], when: Option[HttpPhase]) = {
		new HttpBodyRegExpCheckBuilder(what, to, when, None, None)
	}

	def newInstance(what: Option[Context => String], to: Option[String], when: Option[HttpPhase], checkType: Option[CheckType], expected: Option[String]) = {
		new HttpBodyRegExpCheckBuilder(what, to, when, checkType, expected)
	}

	def build: HttpCheck = new HttpBodyRegExpCheck(what.get, to.get, when.get, checkType.get, expected.get)
}
