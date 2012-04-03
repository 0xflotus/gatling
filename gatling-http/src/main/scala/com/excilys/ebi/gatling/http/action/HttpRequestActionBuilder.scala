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
package com.excilys.ebi.gatling.http.action
import com.excilys.ebi.gatling.core.action.builder.ActionBuilder
import com.excilys.ebi.gatling.core.action.system
import com.excilys.ebi.gatling.core.config.ProtocolConfigurationRegistry
import com.excilys.ebi.gatling.http.check.HttpCheck
import com.excilys.ebi.gatling.http.config.HttpProtocolConfiguration
import com.excilys.ebi.gatling.http.request.HttpRequest

import akka.actor.{ Props, ActorRef }

/**
 * Builder for HttpRequestActionBuilder
 *
 * @constructor creates an HttpRequestActionBuilder
 * @param requestName the name of the request
 * @param request the actual HTTP request that will be sent
 * @param next the next action to be executed
 * @param processorBuilders
 */
class HttpRequestActionBuilder(request: HttpRequest, next: ActorRef, checks: Option[List[HttpCheck]]) extends ActionBuilder {

	private[gatling] def withNext(next: ActorRef) = new HttpRequestActionBuilder(request, next, checks)

	private[gatling] def build(protocolConfigurationRegistry: ProtocolConfigurationRegistry) = {
		val httpConfig = protocolConfigurationRegistry.getProtocolConfiguration(HttpProtocolConfiguration.HTTP_PROTOCOL_TYPE).map(_.asInstanceOf[HttpProtocolConfiguration])
		system.actorOf(Props(new HttpRequestAction(next, request, checks, httpConfig)))
	}
}
