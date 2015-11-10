package services.Actor

import akka.actor.{Actor, ActorLogging}
import common.ConfHelper.ConfigHelper
import common.FileHelper.FileHelper
import common.FqueueHelper.FqueueHelper
import play.api.libs.json._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.{Failure, Success, Try}

/**
 * Created by horatio on 10/27/15.
 */
class SceneActor extends Actor with ActorLogging {
  import services.Actor.LogActor.{Err, Info}
  import services.Actor.RecommendActor.Query
  import services.Actor.SceneActor._
  import services.business.Scenes

  val actorPath = context.self.path.toString.split("/")
  val name = actorPath.last
  val recommend = context.actorSelection("../LogActor")
  val logActor = context.actorSelection("../LogActor")

  def receive = {
    case PullFq =>
      val fqClient = FqueueHelper.client()
      Future(fqClient.pull(queue)) onComplete {
        case Success(record) =>
          if(record == None) {
            logActor ! Info(s"$name: PullFq: no record to pull")
            Thread.sleep(interval)
            self ! PullFq
          } else self ! Judge(record.get, rules)

        case Failure(ex) =>
          logActor ! Err(s"$name: PullFq: $ex")
          Thread.sleep(interval)
          self ! PullFq
      }

    case LoadMap =>
      scenes = ConfigHelper.getConf(scenesFile, separator)
      rules = loadRules(rulesFile, scenes.keys)
      logActor ! Info(s"$rules\n")

    case Judge(record, rules) =>
      Try(Scenes.judge(record, rules, logActor)) match {
        case Success(sceneId) =>
          if (sceneId != "")
            recommend ! Query(sceneId, scenes.toMap)
          else self ! PullFq
        case Failure(ex) =>
          logActor ! Err(s"$name: Judge: $ex")
      }
      Thread.sleep(interval)
      self ! PullFq
  }
}

object SceneActor {
  object PullFq
  object LoadMap
  case class Judge(record: String, rules: Map[String, Map[String, JsValue]])
  object Pause

  def loadRules(rulesFile: String, sceneIds: Iterable[String]): Map[String, Map[String, JsValue]] = {
    import scala.collection.mutable.{Map => muMap}
    val rulesBuffer = Json.parse(FileHelper.readFile(rulesFile)).as[Map[String, JsValue]]
    val rules = muMap[String, Map[String, JsValue]]()

    sceneIds.par map { sceneId =>
      val rule = muMap[String, JsValue]()
      rulesBuffer.keys map { tid =>
        tid.substring(0, 2) match {
          case `sceneId` =>
            val code = tid.substring(3)
            val triggers = rulesBuffer.get(tid).get
            rule += code -> triggers
          case _ =>
        }
      }
      rules += sceneId -> rule.toMap
    }
    rules.toMap
  }

  val dynConfig = ConfigHelper.getConf()
  val queue = dynConfig.getString("Actor.Scene.Fqueue")
  val interval = dynConfig.getString("Actor.Scene.PullInterval").toInt
  val rulesFile = dynConfig.getString("Actor.Scene.rulesFile")
  val scenesFile = dynConfig.getString("Actor.Scene.scenesFile")
  val separator = dynConfig.getString("Actor.Scene.separator")

  var scenes = ConfigHelper.getConf(scenesFile, separator)
  var rules = loadRules(rulesFile, scenes.keys)
}