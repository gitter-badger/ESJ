package services.Actor

import _root_.RecommendActro.Query
import akka.actor.{Actor, ActorLogging, ActorRef}
import common.ConfHelper.ConfigHelper
import common.FqueueHelper.FqueueHelper
import play.api.libs.json._

import scala.collection.mutable.ListBuffer
import scala.concurrent.Future
import scala.util.{Failure, Success, Try}

/**
 * Created by horatio on 10/27/15.
 */
class SceneActor(logActor: ActorRef, maps: ListBuffer[Map[String, Any]]) extends Actor with ActorLogging {
  import services.Actor.LogActor.{Info, Err}
  import services.Actor.SceneActor.{Judge, LoadMap, PullFq}
  import services.business.Scenes

  val name = context.self.path.toString.split("/").last
  val recommend = context.actorSelection("/user/RecommendActor")

  val dynConfig = ConfigHelper.getConf()
  val queue = dynConfig.getString("Actor.Scene.Fqueue")
  val interval = dynConfig.getString("Actor.Scene.PullInterval").toInt

  val rules = Map[String, JsValue]()

  def receive = {
    case PullFq =>
      val fqClient = FqueueHelper.client()
      Future(fqClient.pull(queue)) onComplete {
        case Success(record) =>
          if(record == None) {
            logActor ! Info(s"$name: PullFq: no record to pull")
            Thread.sleep(interval)
            self ! PullFq
          } else self ! LoadMap(record.get)
          
        case Failure(ex: Throwable) =>
          logActor ! Err(s"$name: PullFq: $ex")
          Thread.sleep(interval)
          self ! PullFq
      }

    case LoadMap(record) =>
      val newRules = Map[String, JsValue]()
      if (record == "Load") {
        self ! Judge(record, rules)
      } else self ! Judge(record, rules)


    case Judge(record, queue, interval) =>
      val conditions = maps.apply(0).asInstanceOf[Map[String, JsValue]]

      Try(Scenes.judge(record, conditions).toString) match {
        case Success(sceneId) =>
          if (sceneId != "")
          recommend ! Query(sceneId)
          else self ! PullFq
        case Failure(ex: Throwable) =>
          logActor ! Err(s"$name: Judge: $ex")
      }
      Thread.sleep(5000)
      self ! PullFq
  }
}

object SceneActor {
  object PullFq
  case class LoadMap(record: String)
  case class Judge(record: String, rules: Map[String, JsValue])
  object Pause

  def loadMap(conditions: Map[String, JsValue], sceneId: String): Map[String, JsValue] = {

    import scala.collection.mutable.{Map => mMap}
    val buffer = mMap[String, JsValue]()
    conditions.keys map { tid =>
      tid.substring(0, 2) match {
        case `sceneId` =>
          val code = tid.substring(3)
          val triggers = conditions.get(tid).get
          buffer += code -> triggers
        case _ =>
      }
    }
    buffer.toMap
  }
}