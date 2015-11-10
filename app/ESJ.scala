import akka.actor._
import com.typesafe.config.ConfigFactory
import common.ConfHelper.ConfigHelper
import common.FileHelper.FileHelper
import common.FqueueHelper.FqueueHelper
import services.Actor.SceneActor.PullFq
import services.Actor._

import scala.collection.parallel.ParSeq
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.{Failure, Success}

object ESJ extends App {
  import Boot._

  val dynConfig = ConfigHelper.getConf()
  val queue = dynConfig.getString("Actor.Boot.Fqueue")

  /***** interval to sync maps *****/
  val syncInterval = dynConfig.getString("Actor.Boot.syncInterval").toInt
  val rulesFile = dynConfig.getString("Actor.Scene.rulesFile")
  val scenesFile = dynConfig.getString("Actor.Scene.scenesFile")
  val segSize = dynConfig.getString("Actor.Log.SegmentSize").toInt
  val sysName = dynConfig.getString("App.Name")

  val config = ConfigFactory.load()
  implicit val system = ActorSystem(sysName, config.getConfig("AkkaConfig"))
  val boot = system.actorOf(Props[Boot], "BootActor")
  boot ! Init
}

class Boot extends Actor with ActorLogging {
  import Boot._
  import ESJ._
  import services.Actor.LogActor.{Err, Info}
  import services.Actor.SceneActor.LoadMap

  val name = context.self.path.toString.split("/").last
  val logActor = context.actorOf(Props(classOf[LogActor], segSize), "LogActor")
  context.watch(logActor)

  def receive = {
    case Init =>
      /***** reasonable to pass ActorRef as a parameter? *****/
      Future(ParSeq(
        (Props[MQActor], "MQActor"), (Props[RecommendActor], "RecommendActor"),
        (Props[HBaseActor], "HBaseActor"), (Props[SceneActor], "SceneActor")
      )) onComplete {
        case Success(props) =>
          val actors = props.map(prop =>
            context.watch(
              context.actorOf(prop._1, prop._2))
          ).toArray
          val sceneActor = actors.last
          sceneActor ! PullFq
          self ! SyncMap(sceneActor)

        case Failure(ex) => logActor ! Err(s"$name: Init: $ex")
      }

    case SyncMap(scene) =>
      //if (DateHelper.getCurrentHour.toInt % 8 == 0) { }
      val fqClient = FqueueHelper.client()
      Future(fqClient.pull(queue)) onComplete {
        case Success(map) =>
          if(map != None) {
            FileHelper.save2File(rulesFile, map.get)
            logActor ! Info(s"$name: SyncMap: success to update rule map")
            scene ! LoadMap
          } else logActor ! Info(s"$name: SyncMap: no rule map to update")
        case Failure(ex) => logActor ! Err(s"$name: SyncMap: $ex")
      }

      Thread.sleep(syncInterval)
      self ! SyncMap(scene)

    case Shutdown =>
      val children = context.children
      if (children.isEmpty) {
        logActor ! Info(s"$name: Shutdown: all children already died before, ready to shutdown system")
        self ! PoisonPill
        context.system.shutdown()
      } else {
        children.par.map(child => child ! PoisonPill)
        logActor ! Info(s"$name: Shutdown: killed all children, ready to shutdown system")
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