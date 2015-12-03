package services.actor

import akka.actor.{Actor, ActorLogging}
import common.HBaseHelper.Row

/**
 * Created by horatio on 10/27/15.
 */
class HBaseActor extends Actor with ActorLogging {

  def receive = {
    case _ =>

  }
}

object HBaseActor {
  case class SetRows(rows: Map[String, Row])

}
