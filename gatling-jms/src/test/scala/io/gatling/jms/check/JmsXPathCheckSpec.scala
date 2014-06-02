/**
 * Copyright 2011-2014 eBusiness Information, Groupe Excilys (www.ebusinessinformation.fr)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.gatling.jms.check

import org.specs2.mutable.Specification
import io.gatling.core.validation.{ Failure, Success }
import io.gatling.core.session.Session
import io.gatling.jms.Predef._
import io.gatling.core.Predef._
import io.gatling.jms.{ MockMessage, JmsCheck }
import scala.collection.mutable
import io.gatling.core.config.GatlingConfiguration

class JmsXPathCheckSpec extends Specification with MockMessage {

  GatlingConfiguration.setUp()

  implicit def cache = mutable.Map.empty[Any, Any]

  "xpath check" should {
    val session = Session("mockSession", "mockUserName")
    val check: JmsCheck = xpath("/ok").find

    "return success if condition is true" in {
      check.check(textMessage("<ok></ok>"), session) must beAnInstanceOf[Success[_]]
    }

    "return failure if condition is false" in {
      check.check(textMessage("<ko></ko>"), session) must beAnInstanceOf[Failure]
    }

    "return failure if message is not TextMessage" in {
      check.check(message, session) must beLike {
        case Failure(m) if m contains "Unsupported message type" => ok
      }
    }
  }
}
