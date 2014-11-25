/**
 * Copyright 2011-2014 eBusiness Information, Groupe Excilys (www.ebusinessinformation.fr)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.gatling.core.action.builder

import akka.actor.ActorRef
import io.gatling.core.config.Protocols
import akka.actor.ActorDSL.actor
import io.gatling.core.action.RendezVous

class RendezVousBuilder(users: Int) extends ActionBuilder {

  def build(next: ActorRef, protocols: Protocols) = actor(actorName("rendezVous"))(new RendezVous(users, next))
}
