package services.actor


import akka.actor.{Actor, ActorLogging}
import common.ConfHelper.ConfigHelper
import common.HBaseHelper.{HBaseHelper, Row}
import services.business.ServingLayer
import scala.concurrent.Future
import scala.util.{Failure, Success}
import scala.concurrent.ExecutionContext.Implicits.global


class RecommendActor extends Actor with ActorLogging {

  import services.actor.RecommendActor._
  import services.actor.LogActor.{Err, Info}
  import services.actor.PushActor.{SetToActiveMQ, SetToHBase}

  val actorPath = context.self.path.toString.split("/")
  val name = actorPath.last
  val logActor = context.actorSelection("../LogActor")
  val pushActor = context.actorSelection("../PushActor")
  val noRows = Map[String, Row]()

  def queryOryx(matches: Map[String, Map[String, String]], priorities: Map[String, String]): Map[String, Row] ={
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
    case QueryOryx(matches, priorities) =>
      Future(queryOryx(matches, priorities)) onComplete {
        case Success(rows) =>
          if(rows != noRows)  {
            logActor ! Warn(s"$name: rows: no rows set to pushActor")
            pushActor ! SetToHBase(rows)
            pushActor ! SetToActiveMQ(rows)
          }

        case Failure(ex) =>
          logActor ! Err(s"$name: rows: $ex")
      }

      self ! QueryOryx(matches, priorities)
  }
}

object RecommendActor {
  case class QueryOryx(matches: Map[String, Map[String, String]], priorities: Map[String, String])
  val dynConfig = ConfigHelper.getConf()
  val interval = dynConfig.getString("Actor.Scene.PullInterval").toInt
}
