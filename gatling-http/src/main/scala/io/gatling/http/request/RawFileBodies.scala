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
package io.gatling.http.request

import java.io.{ BufferedInputStream, File => JFile, FileInputStream }

import org.apache.commons.io.FileUtils

import io.gatling.core.config.GatlingConfiguration.configuration
import io.gatling.core.config.GatlingFiles
import io.gatling.core.session.{ Expression, Session }

object RawFileBodies {

	def buildExpression[T](filePath: Expression[String], f: JFile => T): Expression[T] = (session: Session) =>
		for {
			path <- filePath(session)
			file <- GatlingFiles.requestBodyFile(path)
		} yield f(file.jfile)

	def asFile(filePath: Expression[String]): RawFileBody = {
		val expression = buildExpression(filePath, identity)
		new RawFileBody(expression)
	}

	def asString(filePath: Expression[String]): StringBody = {
		val expression = buildExpression(filePath, FileUtils.readFileToString(_, configuration.simulation.encoding))
		new StringBody(expression)
	}

	def asBytes(filePath: Expression[String]): ByteArrayBody = {
		val expression = buildExpression(filePath, FileUtils.readFileToByteArray)
		new ByteArrayBody(expression)
	}

	def asStream(filePath: Expression[String]): InputStreamBody = {
		val expression = buildExpression(filePath, file => new BufferedInputStream(new FileInputStream(file)))
		new InputStreamBody(expression)
	}
}