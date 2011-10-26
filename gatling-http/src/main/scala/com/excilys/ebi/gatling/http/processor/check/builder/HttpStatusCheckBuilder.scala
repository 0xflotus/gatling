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
package com.excilys.ebi.gatling.http.processor.check.builder

import com.excilys.ebi.gatling.core.context.Context
import com.excilys.ebi.gatling.core.processor.CheckType

import com.excilys.ebi.gatling.http.request.HttpPhase._
import com.excilys.ebi.gatling.http.processor.check.HttpCheck
import com.excilys.ebi.gatling.http.processor.check.HttpStatusCheck

import com.excilys.ebi.gatling.core.util.StringHelper._

object HttpStatusCheckBuilder {
	def statusInRange(range: Range) = new HttpStatusCheckBuilder(Some(range.mkString(":")), Some(EMPTY))
	def status(status: Int) = new HttpStatusCheckBuilder(Some(status.toString), Some(EMPTY))
}
class HttpStatusCheckBuilder(expected: Option[String], attrKey: Option[String])
		extends HttpCheckBuilder[HttpStatusCheckBuilder](None, expected, attrKey, Some(StatusReceived), None) {

	def newInstance(expression: Option[Context => String], expected: Option[String], attrKey: Option[String], httpPhase: Option[HttpPhase], checkType: Option[CheckType]) = {
		new HttpStatusCheckBuilder(expected, attrKey)
	}

	def build: HttpCheck = new HttpStatusCheck(expected.get, attrKey.get)
}
