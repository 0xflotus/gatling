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
package com.excilys.ebi.gatling.http.check.status

import scala.annotation.implicitNotFound

import com.excilys.ebi.gatling.core.check.strategy.InRangeCheckStrategy.rangeToString
import com.excilys.ebi.gatling.core.check.strategy.{ InRangeCheckStrategy, EqualityCheckStrategy, CheckStrategy }
import com.excilys.ebi.gatling.core.context.Context
import com.excilys.ebi.gatling.core.util.StringHelper.EMPTY
import com.excilys.ebi.gatling.http.check.{ HttpCheckBuilder, HttpCheck }
import com.excilys.ebi.gatling.http.request.HttpPhase.{ StatusReceived, HttpPhase }

/**
 * HttpStatusCheckBuilder class companion
 *
 * It contains DSL definitions
 */
object HttpStatusCheckBuilder {
	/**
	 * Will check that the response status is in the specified range
	 *
	 * @param range the specified range
	 */
	def statusInRange(range: Range) = new HttpStatusCheckBuilder(None, InRangeCheckStrategy, Some(range))
	/**
	 * Will check that the response status is equal to the one specified
	 *
	 * @param status the expected status code
	 */
	def status(status: Int) = new HttpStatusCheckBuilder(None, EqualityCheckStrategy, Some(status.toString))
}

/**
 * This class builds a response status check
 *
 * @param to the optional context key in which the extracted value will be stored
 * @param strategy the strategy used to check
 * @param expected the expected value against which the extracted value will be checked
 */
class HttpStatusCheckBuilder(to: Option[String], strategy: CheckStrategy, expected: Option[String])
		extends HttpCheckBuilder[HttpStatusCheckBuilder]((c: Context) => EMPTY, to, strategy, expected, StatusReceived) {

	def newInstance(what: Context => String, to: Option[String], strategy: CheckStrategy, expected: Option[String], when: HttpPhase) = new HttpStatusCheckBuilder(to, strategy, expected)

	def build: HttpCheck = new HttpStatusCheck(to, expected)
}
