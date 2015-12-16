package services.actor


import akka.actor.{Actor, ActorLogging}
import common.ConfHelper.ConfigHelper
import common.HBaseHelper.{HBaseHelper, Row}
import common.MQHelper.MQHelper

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.{Failure, Success}


class PushActor extends Actor with ActorLogging {
  import services.actor.LogActor.{Err, Warn}
  import services.actor.PushActor._

  val actorPath = context.self.path.toString.split("/")
  val name = actorPath.last
  val logActor = context.actorSelection("../LogActor")
  val nullRows = Map[String, Row]()

  def receive = {
    case SetToHBase(rows) =>
      Future(rows) onComplete {
        case Success(rows) =>
          if(rows != nullRows)  {
            logActor ! Warn(s"$name: SetToHBase: no rows set to HBase")
            HBaseHelper.setRows(sceneTable, rows)
          }
        case Failure(ex) =>
          logActor ! Err(s"$name: SetToHBase: $ex")
      }
      self ! SetToHBase(rows)

    case SetToActiveMQ(rows) =>
      Future(rows) onComplete {
        case Success(rows) =>
          if(rows != nullRows)  {
            logActor ! Warn(s"$name: SetToActiveMQ: no rows set to MQ")
            rows.keys foreach { uid =>
              val row = rows(uid)
              val qualifersAndValues = row.qualifersAndValues

              val jsStr = s"""{ "uid":"${uid}, "TemplateId":${qualifersAndValues("TemplateId")}, "SendTime": ${qualifersAndValues("SendTime")}, "Tags": ${qualifersAndValues("Tags")}, "Items": ${qualifersAndValues("Items")}, "Priority":${qualifersAndValues("Priority")}}"""
              MQHelper.getMqueue.sendQueue(queue, jsStr)
            }
          }

        case Failure(ex) =>
          logActor ! Err(s"$name: SetToActiveMQ: $ex")
      }
      self ! SetToActiveMQ(rows)
  }
}


object PushActor {
  case class SetToHBase(rows: Map[String, Row])
  case class SetToActiveMQ(rows: Map[String, Row])
  val dynConfig = ConfigHelper.getConf()
  val sceneTable = dynConfig.getString("Actor.Push.SceneTable")
  val queue = dynConfig.getString("Actor.Push.ActiveMQ")
}
