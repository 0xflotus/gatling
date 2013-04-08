/**
 * Copyright 2011-2013 eBusiness Information, Groupe Excilys (www.excilys.com)
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
package io.gatling.http.check

import io.gatling.core.check.{ CheckFactory, Extractor, ExtractorCheckBuilder, MatcherCheckBuilder, Preparer }
import io.gatling.core.session.Expression
import io.gatling.http.response.Response

class HttpSingleCheckBuilder[P, T, X](
	checkFactory: CheckFactory[HttpCheck, Response],
	preparer: Preparer[Response, P],
	extractor: Extractor[P, T, X],
	expression: Expression[T]) extends ExtractorCheckBuilder[HttpCheck, Response, P, T, X] {

	def find: MatcherCheckBuilder[HttpCheck, Response, P, T, X] = MatcherCheckBuilder(checkFactory, preparer, extractor, expression)
}