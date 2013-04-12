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
package io.gatling.metrics.sender

import java.io.BufferedOutputStream
import java.net.Socket

import io.gatling.core.config.GatlingConfiguration.configuration
import io.gatling.core.util.StringHelper.eol

class TcpSender extends MetricsSender {

	val socket = new Socket(configuration.graphite.host, configuration.graphite.port)
	val os = new BufferedOutputStream(socket.getOutputStream)

	def sendToGraphite(bytes: Array[Byte]) {
		os.write(bytes)
	}

	def flush {
		os.flush
	}

	def close {
		os.close
	}
}
