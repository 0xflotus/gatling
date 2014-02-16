/**
 * Copyright 2011-2012 eBusiness Information, Groupe Excilys (www.excilys.com)
 * Copyright 2012 Gilt Groupe, Inc. (www.gilt.com)
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
package io.gatling.http.request.builder

import io.gatling.core.session.{ Expression, SessionPrivateAttributes }
import io.gatling.http.action.ws.{ CloseWebSocketActionBuilder, SendWebSocketMessageActionBuilder }
import io.gatling.http.request.builder.WebSocket.defaultWebSocketName

object WebSocket {

	val defaultWebSocketName = SessionPrivateAttributes.privateAttributePrefix + "http.webSocket"
}

/**
 * @param actionName The action name in the log
 */
class WebSocket(requestName: Expression[String]) {

	/**
	 * Opens a web socket and stores it in the session.
	 *
	 * @param url The socket URL
	 * @param attributeName The name of the session attribute used to store the socket
	 */
	def open(url: Expression[String], wsName: String = defaultWebSocketName) = new OpenWebSocketRequestBuilder(CommonAttributes(requestName, "GET", Left(url)), wsName)

	/**
	 * Sends a message on the given socket.
	 *
	 * @param fMessage The message
	 * @param attributeName The name of the session attribute storing the socket
	 */
	def sendMessage(message: Expression[String], wsName: String = defaultWebSocketName) = new SendWebSocketMessageActionBuilder(requestName, wsName, message)

	/**
	 * Closes a web socket.
	 *
	 * @param attributeName The name of the session attribute storing the socket
	 */
	def close(wsName: String = defaultWebSocketName) = new CloseWebSocketActionBuilder(requestName, wsName)
}
