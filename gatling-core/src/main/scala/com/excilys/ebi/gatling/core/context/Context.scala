package com.excilys.ebi.gatling.core.context

import akka.actor.Uuid
import com.excilys.ebi.gatling.core.log.Logging

class Context(val userId: Int, val writeActorUuid: Uuid, val feederIndex: Int, var data: Map[String, String]) extends Logging {
  def getUserId = userId
  def getWriteActorUuid = writeActorUuid
  def getFeederIndex = feederIndex

  def getData = data

  def getAttribute(key: String): String = {
    val result = data.get(key).getOrElse(throw new Exception("No matching attribute"))
    logger.debug("Context('{}') = {}", key, result)
    logger.debug("Context: {}", data)
    result
  }

  def setAttributes(attributes: Map[String, String]) = {
    data ++= attributes
  }

  def getElapsedActionTime: Long =
    data.get("gatlingElapsedTime").map { l =>
      l.asInstanceOf[Long]
    }.getOrElse(0L)
}