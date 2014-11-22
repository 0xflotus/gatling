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
package io.gatling.core.pause

import scala.concurrent.duration.Duration
import scala.concurrent.forkjoin.ThreadLocalRandom

import org.uncommons.maths.random.{ UnsafeXORShiftRNG, ExponentialGenerator }

import io.gatling.core.session.{ Expression, ExpressionWrapper }

sealed abstract class PauseType {
  def generator(duration: Duration): Expression[Long] = generator(duration.expression)
  def generator(duration: Expression[Duration]): Expression[Long]
}

object Disabled extends PauseType {
  def generator(duration: Expression[Duration]) = throw new UnsupportedOperationException
}

object Constant extends PauseType {
  def generator(duration: Expression[Duration]) = duration.map(_.toMillis)
}

object Exponential extends PauseType {

  val Generator = new ThreadLocal[ExponentialGenerator] {
    override def initialValue = new ExponentialGenerator(1.0, new UnsafeXORShiftRNG)
  }

  def generator(duration: Expression[Duration]) = duration.map(duration => math.round(Generator.get.nextValue * duration.toMillis))
}

case class Custom(custom: Expression[Long]) extends PauseType {
  def generator(duration: Expression[Duration]) = custom
}

case class UniformPercentage(plusOrMinus: Double) extends PauseType {
  def generator(duration: Expression[Duration]) = duration.map { duration =>
    val mean = duration.toMillis
    val halfWidth = math.round(mean * plusOrMinus / 100.0)
    val least = mean - halfWidth
    val bound = mean + halfWidth + 1
    ThreadLocalRandom.current.nextLong(least, bound)
  }
}

case class UniformDuration(plusOrMinus: Duration) extends PauseType {
  def generator(duration: Expression[Duration]) = duration.map { duration =>
    val mean = duration.toMillis
    val halfWidth = plusOrMinus.toMillis
    val least = math.max(mean - halfWidth, 0L)
    val bound = mean + halfWidth + 1
    ThreadLocalRandom.current.nextLong(least, bound)
  }
}
