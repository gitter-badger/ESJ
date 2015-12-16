package services.actor

import akka.actor.{Actor, ActorLogging}
import common.ConfHelper.ConfigHelper
import common.HBaseHelper.{HBaseHelper, Row}
import common.MQHelper.MQHelper

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future


class PushActor extends Actor with ActorLogging {
  import services.actor.LogActor.{Err, Warn}
  import services.actor.PushActor._

  val actorPath = context.self.path.toString.split("/")
  val name = actorPath.last
  val logActor = context.actorSelection("../LogActor")
  val nullRows = Map[String, Row]()

  def setToHBase(rows: Map[String, Row]) = {
    if (rows != nullRows) {
      HBaseHelper.setRows("testTable", rows)
    } else {
      logActor ! Warn(s"$name: rows: no rows set to HBase")
    }
  }

  def setToActiveMQ(rows: Map[String, Row]) = {
    if (rows != nullRows) {
      rows.keys foreach(uid =>{
        val row = rows(uid)
        val qualifersAndValues = row.qualifersAndValues
        val jsStr = s"""{ "uid":"${uid}", "TemplateId":"${qualifersAndValues("TemplateId")}", "SendTime": "${qualifersAndValues("SendTime")}", "Tags": "${qualifersAndValues("Tags")}", "Items": "${qualifersAndValues("Items")}", "Prioritie": "${qualifersAndValues("Prioritie")}"}"""
        val mqueue =MQHelper.getMqueue()
        mqueue.sendQueue("test", jsStr)
      })
    } else {
      logActor ! Warn(s"$name: rows: no rows set to MQ")
    }
  }

  def receive = {
    case SetToHBase(rows) =>
      Future(setToHBase(rows)) onFailure {
        case ex =>
          logActor ! Err(s"$name: rows: $ex")
          self ! SetToHBase(rows)
      }

    case SetToActiveMQ(rows) =>
      Future(setToActiveMQ(rows)) onFailure {
        case ex =>
          logActor ! Err(s"$name: rows: $ex")
          self ! SetToActiveMQ(rows)
      }

  }
}


object PushActor {
  case class SetToHBase(rows: Map[String, Row])
  case class SetToActiveMQ(rows: Map[String, Row])
  val dynConfig = ConfigHelper.getConf()
}
