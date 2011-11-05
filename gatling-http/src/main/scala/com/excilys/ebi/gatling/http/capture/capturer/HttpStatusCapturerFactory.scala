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

import com.excilys.ebi.gatling.core.capture.capturer.CapturerFactory
import com.ning.http.client.Response

object HttpStatusCapturerFactory extends CapturerFactory[Response] {

	def getCapturer(response: Response) = {
		logger.debug("Instantiation of HttpStatusCaptureProvider")
		new HttpStatusCapturer(response)
	}
}