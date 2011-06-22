package com.excilys.ebi.gatling.core.action.builder

import com.excilys.ebi.gatling.core.action.Action
import com.excilys.ebi.gatling.core.action.EndAction

import akka.actor.TypedActor

object EndActionBuilder {
  class EndActionBuilder extends AbstractActionBuilder {

    def build(): Action = {
      println("Building EndAction")
      TypedActor.newInstance(classOf[Action], classOf[EndAction])
    }

    def withNext(next: Action): AbstractActionBuilder = this

    override def toString = "End"
  }

  def endActionBuilder = new EndActionBuilder
}