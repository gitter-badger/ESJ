package services.actor

import akka.actor.{Actor, ActorLogging}
import common.ConfHelper.ConfigHelper
import common.HBaseHelper.{HBaseHelper, Row}
import scala.util.{Failure, Success}
import services.actor.LogActor.{Err, Info}
import scala.concurrent.Future

/**
 * Created by horatio on 10/27/15.
 */
class HBaseActor extends Actor with ActorLogging {

  import services.actor.HBaseActor._

  val noRows = Map[String, Row]()
  val logActor = context.actorSelection("../LogActor")

  def receive = {
    case SetRows(rows) =>
      Future(rows) onComplete {
        case Success(rows) =>
          if(rows != noRows)  {
            logActor ! Info(s"")
            HBaseHelper.setRows("", rows)
          }
        case Failure(ex) =>
          logActor ! Err(s"")
          Thread.sleep(interval)
      }
      Thread.sleep(interval)
      self ! SetRows(rows)

  }
}

object HBaseActor {
  case class SetRows(rows: Map[String, Row])
  val dynConfig = ConfigHelper.getConf()
  val interval = dynConfig.getString("Actor.Scene.PullInterval").toInt

}
