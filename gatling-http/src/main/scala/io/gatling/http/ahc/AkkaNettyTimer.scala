/**
 * Copyright 2011-2014 eBusiness Information, Groupe Excilys (www.ebusinessinformation.fr)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.gatling.http.ahc

import java.util.concurrent.atomic.AtomicReference

import scala.concurrent.duration._

import org.jboss.netty.util.{Timeout, TimerTask, Timer}
import akka.actor.ActorSystem

class AkkaNettyTimer(system: ActorSystem) extends Timer {

  def newTimeout(task: TimerTask, delay: Long, unit: TimeUnit): Timeout = {

    val timeoutRef = new AtomicReference[Timeout]

    def timeoutRefValue: Timeout = {
      var value: Timeout = null
      do {
        value = timeoutRef.get
      } while (value == null)
      value
    }

    val cancellable = system.scheduler.scheduleOnce(unit.toNanos(delay) nanoseconds) {
      task.run(timeoutRefValue)
    }

    val timeout = new Timeout {
      def getTimer: Timer = throw new UnsupportedOperationException("getTimer is not supported")

      def getTask: TimerTask = task

      def isExpired: Boolean = throw new UnsupportedOperationException("isExpired is not supported")

      def isCancelled: Boolean = cancellable.isCancelled

      def cancel() {
        cancellable.cancel()
      }
    }

    timeoutRef.set(timeout)

    timeout
  }

  def stop: java.util.Set[Timeout] = throw new UnsupportedOperationException("stop is not supported")
}
