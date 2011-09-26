package com.excilys.ebi.gatling.core.action.builder

import com.excilys.ebi.gatling.core.action.PauseAction
import com.excilys.ebi.gatling.core.action.Action

import com.excilys.ebi.gatling.core.scenario.builder.ScenarioBuilder

import java.util.concurrent.TimeUnit

import akka.actor.TypedActor

object PauseActionBuilder {
  class PauseActionBuilder(val minDuration: Option[Long], val maxDuration: Option[Long], val timeUnit: Option[TimeUnit], val next: Option[Action]) extends AbstractActionBuilder {

    def withMinDuration(minDuration: Long) = new PauseActionBuilder(Some(minDuration), maxDuration, timeUnit, next)

    def withMaxDuration(maxDuration: Long) = new PauseActionBuilder(minDuration, Some(maxDuration), timeUnit, next)

    def withDuration(duration: Long) = new PauseActionBuilder(Some(duration), Some(duration), timeUnit, next)

    def withTimeUnit(timeUnit: TimeUnit) = new PauseActionBuilder(minDuration, maxDuration, Some(timeUnit), next)

    def withNext(next: Action) = new PauseActionBuilder(minDuration, maxDuration, timeUnit, Some(next))

    def build(scenarioId: Int): Action = {
      logger.debug("Building PauseAction with duration in : {} - {}ms", TimeUnit.MILLISECONDS.convert(minDuration.get, timeUnit.get), TimeUnit.MILLISECONDS.convert(maxDuration.get, timeUnit.get))
      ScenarioBuilder.addToExecutionTime(scenarioId, maxDuration.get, timeUnit.get)
      TypedActor.newInstance(classOf[Action], new PauseAction(next.get, minDuration.get, maxDuration.get, timeUnit.get))
    }
  }

  def pauseActionBuilder = new PauseActionBuilder(None, None, Some(TimeUnit.SECONDS), None)

}