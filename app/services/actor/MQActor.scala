package services.actor

import akka.actor.{Actor, ActorLogging}
import common.ConfHelper.ConfigHelper
import common.HBaseHelper.Row
import common.MQHelper.MQHelper
import services.actor.HBaseActor.SetRows
import services.actor.RecommendActor.SetMQ
import scala.concurrent.Future
import scala.util.{Failure, Success}
import services.actor.LogActor.{Err, Info}

class MQActor extends Actor with ActorLogging {

  import services.actor.MQActor._

  val logActor = context.actorSelection("../LogActor")
  val noRows = Map[String, Row]()

  def setRows(qname: String, rows: Map[String, Row]) {
    rows.keys foreach(uid =>{
      val row = rows(uid)
      val qualifersAndValues = row.qualifersAndValues
      val jsStr = s"""{ "uid":"${uid}, "TemplateId":"${qualifersAndValues("TemplateId")}", "SendTime": "${qualifersAndValues("SendTime")}", "Tags": "${qualifersAndValues("Tags")}", "Items": "${qualifersAndValues("Items")}""Prioritie":"${qualifersAndValues("Prioritie")}"}"""
      val mqueue =MQHelper.getMqueue()
      mqueue.sendQueue(qname, jsStr)
    })
  }
  def receive = {
    case SetMQ(rows) =>
      Future(rows) onComplete {
        case Success(rows) =>
          if(rows != noRows)  {
            logActor ! Info(s"")
            setRows("test", rows)
          }
        case Failure(ex) =>
          logActor ! Err(s"")
          Thread.sleep(interval)
      }
      Thread.sleep(interval)
      self ! SetRows(rows)
  }
}
object MQActor {

  val dynConfig = ConfigHelper.getConf()
  val interval = dynConfig.getString("Actor.Scene.PullInterval").toInt
}