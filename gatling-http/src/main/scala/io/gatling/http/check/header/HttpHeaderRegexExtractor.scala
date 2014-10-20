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
package io.gatling.http.check.header

import io.gatling.core.check.extractor._
import io.gatling.core.check.extractor.regex.{ GroupExtractor, RegexExtractor }
import io.gatling.core.validation.{ SuccessWrapper, Validation }
import io.gatling.http.response.Response

abstract class HttpHeaderRegexExtractor[X] extends CriterionExtractor[Response, (String, String), X] with RegexExtractor {

  val criterionName = "headerRegex"

  def extractHeadersValues[F](response: Response, headerNameAndPattern: (String, String))(implicit groupExtractor: GroupExtractor[F]) = {
    val (headerName, pattern) = headerNameAndPattern
    val headerValues = HttpHeaderExtractor.decodedHeaders(response, headerName)
    headerValues.map(extractAll(_, pattern)).flatten
  }
}

class SingleHttpHeaderRegexExtractor[X](val criterion: (String, String), val occurrence: Int)(implicit groupExtractor: GroupExtractor[X]) extends HttpHeaderRegexExtractor[X] with FindArity {

  def extract(prepared: Response): Validation[Option[X]] =
    extractHeadersValues(prepared, criterion).lift(occurrence).success
}

class MultipleHttpHeaderRegexExtractor[X](val criterion: (String, String))(implicit groupExtractor: GroupExtractor[X]) extends HttpHeaderRegexExtractor[Seq[X]] with FindAllArity {

  def extract(prepared: Response): Validation[Option[Seq[X]]] =
    extractHeadersValues(prepared, criterion).liftSeqOption.success
}

class CountHttpHeaderRegexExtractor(val criterion: (String, String)) extends HttpHeaderRegexExtractor[Int] with CountArity {

  def extract(prepared: Response): Validation[Option[Int]] =
    extractHeadersValues[String](prepared, criterion).liftSeqOption.map(_.size).success
}
