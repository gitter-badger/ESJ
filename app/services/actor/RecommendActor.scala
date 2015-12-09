package services.actor


import akka.actor.{Actor, ActorLogging}
import common.ConfHelper.ConfigHelper
import common.HBaseHelper.{HBaseHelper, Row}
import services.actor.HBaseActor.SetRows
import services.actor.LogActor.{Err, Info}
import services.business.ServingLayer

import scala.concurrent.Future
import scala.util.{Failure, Success}


class RecommendActor extends Actor with ActorLogging {

  import services.actor.RecommendActor._

  val logActor = context.actorSelection("../LogActor")
  val MQActor = context.actorSelection("../MQActor")
  val HBaseActor = context.actorSelection("../HBaseActor
  val noRows = Map[String, Row]()

  def rec(matches: Map[String, Map[String, String]], priorities: Map[String, String]): Map[String, Row] ={
    import scala.collection.mutable.{Map => muMap}
    val rows = muMap[String, Row]()
    matches.keys foreach(uid =>{
      val value = matches.get(uid).get
      val templateId = value.get("TemplateId").get
      val sendTime = value.get("SendTime").get
      val mprioritie = priorities.get(templateId).get.toInt
      val rowPull = HBaseHelper.getRow("CWX_table", Iterable(uid)).get(uid).get
      if (rowPull != null) {
        val qav = rowPull.qualifersAndValues
        val tprioritie = qav.get("Prioritie").get.toInt
        if (mprioritie > tprioritie) {
          val family = rowPull.family
          val items = ServingLayer.getItemsByUid(uid, "10")
          val tags = ServingLayer.getTagsByUid(uid, "10")
          val qualifersAndValues = muMap[String, String]()
          qualifersAndValues += ("TemplateId" -> templateId)
          qualifersAndValues += ("SendTime" -> sendTime)
          qualifersAndValues += ("Tags" -> tags)
          qualifersAndValues += ("Items" -> items)
          qualifersAndValues += ("Prioritie" -> mprioritie.toString)
          val row = new Row(uid, family, qualifersAndValues.toMap)
          rows += (uid -> row)
        }
      }
    })
    rows.toMap
  }

  def receive = {
    case Query(matches, priorities) =>
      Future(rec(matches, priorities)) onComplete {
        case Success(rows) =>
          if(rows != noRows)  {
            logActor ! Info(s"")
            HBaseActor ! SetRows(rows)
            MQActor ! SetMQ(rows)
          }
        case Failure(ex) =>
          logActor ! Err(s"")
          Thread.sleep(interval)
      }
      Thread.sleep(interval)
      self ! Query(matches, priorities)

  }
}

object RecommendActor {
  case class Query(matches: Map[String, Map[String, String]], priorities: Map[String, String])
  case class SetMQ(rows: Map[String, Row])
  val dynConfig = ConfigHelper.getConf()
  val interval = dynConfig.getString("Actor.Scene.PullInterval").toInt

}