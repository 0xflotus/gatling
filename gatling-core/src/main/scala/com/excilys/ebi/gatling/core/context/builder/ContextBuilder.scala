package com.excilys.ebi.gatling.core.context.builder

import com.excilys.ebi.gatling.core.log.Logging
import com.excilys.ebi.gatling.core.context.Context

import akka.actor.Uuid

abstract class TRUE

object ContextBuilder {
  class ContextBuilder[HUID, HWAU](val userId: Option[Int], val writeActorUuid: Option[Uuid], val data: Option[Map[String, String]], val feederIndex: Option[Int])
    extends Logging {

    def fromContext(context: Context) = new ContextBuilder[TRUE, TRUE](Some(context.getUserId), Some(context.getWriteActorUuid), Some(context.getData), Some(context.getFeederIndex))

    def withUserId(userId: Int) = new ContextBuilder[TRUE, HWAU](Some(userId), writeActorUuid, data, feederIndex)

    def withWriteActorUuid(writeActorUuid: Uuid) = new ContextBuilder[HUID, TRUE](userId, Some(writeActorUuid), data, feederIndex)

    def withData(data: Map[String, String]) = new ContextBuilder[HUID, HWAU](userId, writeActorUuid, Some(data), feederIndex)

    def setAttribute(attr: Tuple2[String, String]) = new ContextBuilder[HUID, HWAU](userId, writeActorUuid, Some(data.get + (attr._1 -> attr._2)), feederIndex)

    def unsetAttribute(attrKey: String) = new ContextBuilder[HUID, HWAU](userId, writeActorUuid, Some(data.get - attrKey), feederIndex)

    def getAttribute(attrKey: String) = data.get.get(attrKey)

    def setElapsedActionTime(value: Long) = unsetAttribute("gatlingElapsedTime") setAttribute ("gatlingElapsedTime", value.toString)

    def withFeederIndex(feederIndex: Int) = new ContextBuilder[HUID, HWAU](userId, writeActorUuid, data, Some(feederIndex))
  }

  implicit def enableBuild(builder: ContextBuilder[TRUE, TRUE]) = new {
    def build(): Context = {
      val context = new Context(builder.userId.get, builder.writeActorUuid.get, builder.feederIndex.getOrElse(-1), builder.data.get)
      builder.logger.debug("Built Context")
      context
    }
  }

  def makeContext = new ContextBuilder(None, None, Some(Map()), None)
}