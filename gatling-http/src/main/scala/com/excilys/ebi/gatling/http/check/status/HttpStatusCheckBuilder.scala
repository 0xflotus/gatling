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

import com.excilys.ebi.gatling.core.check.strategy.CheckStrategy
import com.excilys.ebi.gatling.core.check.strategy.InRangeCheckStrategy
import com.excilys.ebi.gatling.core.check.CheckBuilderFind
import com.excilys.ebi.gatling.core.check.CheckBuilderVerifyOne
import com.excilys.ebi.gatling.core.check.CheckBuilderSave
import com.excilys.ebi.gatling.core.context.Context
import com.excilys.ebi.gatling.core.util.StringHelper.EMPTY
import com.excilys.ebi.gatling.http.check.HttpCheck
import com.excilys.ebi.gatling.http.check.HttpCheckBuilder
import com.excilys.ebi.gatling.http.request.HttpPhase.HttpPhase
import com.excilys.ebi.gatling.http.request.HttpPhase.StatusReceived

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
	def status = new HttpStatusCheckBuilder(InRangeCheckStrategy, Nil, None) with CheckBuilderFind[HttpCheckBuilder[HttpStatusCheckBuilder]]
}

/**
 * This class builds a response status check
 *
 * @param to the optional context key in which the extracted value will be stored
 * @param strategy the strategy used to check
 * @param expected the expected value against which the extracted value will be checked
 */
class HttpStatusCheckBuilder(strategy: CheckStrategy, expected: List[String], saveAs: Option[String])
		extends HttpCheckBuilder[HttpStatusCheckBuilder]((c: Context) => EMPTY, None, strategy, expected, saveAs, StatusReceived) {

	private[http] def newInstance(what: Context => String, occurrence: Option[Int], strategy: CheckStrategy, expected: List[String], saveAs: Option[String], when: HttpPhase) =
		new HttpStatusCheckBuilder(strategy, expected, saveAs)

	private[gatling] def newInstanceWithFindOne(occurrence: Int) =
		new HttpStatusCheckBuilder(strategy, expected, saveAs) with CheckBuilderVerifyOne[HttpCheckBuilder[HttpStatusCheckBuilder]]

	private[gatling] def newInstanceWithFindAll = throw new UnsupportedOperationException("Status checks are single valued")

	private[gatling] def newInstanceWithVerify(strategy: CheckStrategy, expected: List[String] = Nil) =
		new HttpStatusCheckBuilder(strategy, expected, saveAs) with CheckBuilderSave[HttpCheckBuilder[HttpStatusCheckBuilder]]

	private[gatling] def build: HttpCheck = new HttpStatusCheck(expected, saveAs)
}
