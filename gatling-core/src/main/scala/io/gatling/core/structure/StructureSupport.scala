/**
 * Copyright 2011-2013 eBusiness Information, Groupe Excilys (www.ebusinessinformation.fr)
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
package io.gatling.core.structure

import io.gatling.core.action.builder.ActionBuilder
import io.gatling.core.config.Protocols

trait StructureSupport extends StructureBuilder[ChainBuilder] {

	private[core] def newInstance(actionBuilders: List[ActionBuilder], defaultProtocols: Protocols) = new ChainBuilder(actionBuilders, defaultProtocols)

	private[core] def actionBuilders: List[ActionBuilder] = Nil
	private[core] def defaultProtocols = Protocols()
}
