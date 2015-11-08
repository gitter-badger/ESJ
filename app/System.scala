import akka.actor._
import com.typesafe.config.ConfigFactory
import common.ConfHelper.ConfigHelper
import common.FileHelper.FileHelper
import common.FqueueHelper.FqueueHelper
import services.Actor._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.{Failure, Success}

object System extends App {
  import Boot._

  val dynConfig = ConfigHelper.getConf()
  val queue = dynConfig.getString("Actor.Boot.Fqueue")

  /***** interval to sync maps *****/
  val syncInterval = dynConfig.getString("Actor.Boot.syncInterval").toInt
  val rulesFile = dynConfig.getString("Actor.Scene.rulesFile")
  val scenesFile = dynConfig.getString("Actor.Scene.scenesFile")
  val segSize = dynConfig.getString("Actor.Log.SegmentSize").toInt

  val config = ConfigFactory.load()
  val system = ActorSystem("EmailSceneJudge", config.getConfig("AkkaConfig"))
  val boot = system.actorOf(Props[Boot], "BootActor")
  boot ! Init
}

class Boot extends Actor with ActorLogging {
  import Boot._
  import System._
  import services.Actor.LogActor.{Err, Info}
  import services.Actor.SceneActor.{PullFq, LoadMap}

  val name = context.self.path.toString.split("/").last
  val logActor = context.actorOf(Props(classOf[LogActor], segSize), "LogActor")
  context.watch(logActor)

  def receive = {
    case Init =>
      val scene = context.actorOf(Props(classOf[SceneActor], LogActor), "SceneActor")
      context.watch(scene)
      val recommend = context.actorOf(Props(classOf[RecommendActor], LogActor), "RecommendActor")
      context.watch(recommend)
      val hBase = context.actorOf(Props(classOf[HBaseActor], LogActor), "HBaseActor")
      context.watch(logActor)
      val mQ = context.actorOf(Props(classOf[MQActor], LogActor), "MQActor")
      context.watch(mQ)

      scene ! PullFq
      self ! SyncMap(scene)

    case SyncMap(scene) =>
      //if (DateHelper.getCurrentHour.toInt % 8 == 0) { }
      log.info("time to sync maps")
      val fqClient = FqueueHelper.client()
      Future(fqClient.pull(queue)) onComplete {
        case Success(map) =>
          if(map != None) {
            FileHelper.save2File(rulesFile, map.get)
            scene ! LoadMap
          } else logActor ! Info(s"$name: SyncMap: no rule map to update")
        case Failure(ex) => logActor ! Err(s"$name: SyncMap: $ex")
      }

      Thread.sleep(syncInterval)
      self ! SyncMap(scene)

    case Shutdown =>
      if (context.children.isEmpty) {
        logActor ! Info(s"$name: Shutdown: all children died, ready to shutdown system")
        self ! PoisonPill
        context.system.shutdown()
      }
  }
}

object Boot {
  object Init
  case class SyncMap(scene: ActorRef)
  object Shutdown
}