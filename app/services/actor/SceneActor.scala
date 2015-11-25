package services.actor

import akka.actor.{Actor, ActorLogging}
import common.ConfHelper.ConfigHelper
import common.FileHelper.FileHelper
import common.FqueueHelper.FqueueHelper
import play.api.libs.json._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.{Failure, Success}

/** Created by horatio on 10/27/15.
  */
class SceneActor extends Actor with ActorLogging {

  import services.actor.LogActor.{Err, Info, Warn}
  import services.actor.RecommendActor.Query
  import services.actor.SceneActor._
  import services.business.Scenes

  val actorPath = context.self.path.toString.split("/")
  val name = actorPath.last
  val logActor = context.actorSelection("../LogActor")
  val recommend = context.actorSelection("../LogActor")
  val noMatches = Map[String, Map[String, String]]()

  def receive = {
    case PullFq =>
      val fqClient = FqueueHelper.client()
      Future(fqClient.pull(queue)) onComplete {
        case Success(records) =>
          if (records.isEmpty) {
            logActor ! Warn(s"$name: PullFq: no records to pull")
            Thread.sleep(interval)
            self ! PullFq
          }
          else self ! Judge(records.get, rules)

        case Failure(ex) =>
          logActor ! Err(s"$name: PullFq: $ex")
          Thread.sleep(interval)
          self ! PullFq
      }

    case LoadMap =>

      /**
       * rules: Map[SceneId, Trigger] = Map[SceneId, Map[Variable, Value]]
       * sceneIds: Map[SceneId, Priority]
       */
      sceneIds = ConfigHelper.getMap(sceneIdsFile, separator)
      rules = loadRules(rulesFile, sceneIds.values)
      logActor ! Info(s"$rules\n")

    case Judge(records, rules) =>

      /**
       * Scenes.juage return a composite Map: Map[Uid, Map[SceneId, SendTime]]
       */
      Future(Scenes.judge(records, rules, sceneIds)) onComplete {
        case Success(matches) =>
          if (matches != noMatches) {
            logActor ! Info(s"$name: Judge: $matches")
            recommend ! Query(matches, priorities)
          }
        case Failure(ex) =>
          logActor ! Err(s"$name: Judge: $ex")
          Thread.sleep(interval)
      }
      Thread.sleep(interval)
      self ! PullFq
  }
}

object SceneActor {

  object PullFq

  object LoadMap

  case class Judge(records: String, rules: Map[String, Map[String, JsValue]])

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
  val priorityFile = dynConfig.getString("Actor.Recommend.Priority")
  val rulesFile = dynConfig.getString("Actor.Scene.Rules")
  val sceneIdsFile = dynConfig.getString("Actor.Scene.Identities")
  val separator = dynConfig.getString("Actor.Scene.Separator")

  var priorities = ConfigHelper.getMap(priorityFile, separator)
  var sceneIds = ConfigHelper.getMap(sceneIdsFile, separator)
  var rules = loadRules(rulesFile, sceneIds.values)
}