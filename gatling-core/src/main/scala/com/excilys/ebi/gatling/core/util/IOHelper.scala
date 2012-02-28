/**
 * Copyright 2011-2012 eBusiness Information, Groupe Excilys (www.excilys.com)
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
package com.excilys.ebi.gatling.core.util
import java.io.{ OutputStream, InputStream, Closeable }
import scala.annotation.tailrec

object IOHelper {

	val BYTE_BUFFER_SIZE = 1024 * 4

	def use[T, C <: Closeable](closeable: C)(block: C => T) = {
		try
			block(closeable)
		finally
			closeable.close
	}

	def copy(input: InputStream, output: OutputStream) {

		@tailrec
		def copyRec(input: InputStream, output: OutputStream, buffer: Array[Byte]) {
			val n = input.read(buffer)
			if (n != -1) {
				output.write(buffer, 0, n)
				copyRec(input, output, buffer)
			}
		}

		use(input) { input =>
			use(output) { output =>
				copyRec(input, output, new Array[Byte](BYTE_BUFFER_SIZE))
			}
		}
	}
}