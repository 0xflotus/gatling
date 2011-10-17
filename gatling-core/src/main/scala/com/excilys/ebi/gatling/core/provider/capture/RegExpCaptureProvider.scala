/*
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
package com.excilys.ebi.gatling.core.provider.capture

import scala.util.matching.Regex

/**
 * This class is a built-in provider that helps searching with Regular Expressions
 *
 * @constructor creates a new RegExpCaptureProvider
 * @param textContent the text where the search will be made
 */
class RegExpCaptureProvider(textContent: String) extends AbstractCaptureProvider {
	/**
	 * The actual capture happens here. The regular expression is compiled and the first
	 * result is returned if existing.
	 *
	 * @param expression a String containing the regular expression to be matched
	 * @return an option containing the value if found, None otherwise
	 */
	def capture(expression: Any): Option[String] = {
		logger.debug("[RegExpCaptureProvider] Capturing with expression : {}", expression)
		new Regex(expression.toString).findFirstMatchIn(textContent).map { m =>
			if (m.groupCount > 0)
				m.group(1)
			else
				m.matched
		}
	}
}