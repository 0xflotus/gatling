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
package io.gatling.core.util

import java.nio.charset.Charset

object StandardCharsets {

  val UTF_8 = Charset.forName("UTF-8")
  val UTF_16 = Charset.forName("UTF-16")
  val UTF_32 = Charset.forName("UTF-32")
  val ASCII = Charset.forName("ASCII")
  val US_ASCII = Charset.forName("US-ASCII")
  val ISO_8859_1 = Charset.forName("ISO-8859-1")
}
