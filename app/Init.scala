/**
 * Created by horatio on 10/27/15.
 */

import akka.actor._
import com.typesafe.config.ConfigFactory
import common.ConfHelper.ConfigHelper
import common.FileHelper.FileHelper
import common.FqueueHelper.FqueueHelper
import play.api.libs.json.{JsValue, Json}
import services.Actor.JudgeActor.{Pause, PullFq}
import services.Actor.LogActor.Err
import services.Actor.{JudgeActor, LogActor}

import scala.collection.mutable.ListBuffer
import scala.util.{Failure, Success, Try}

object Init {

  protected def loadMap: ListBuffer[Map[String, Any]] = {
    val maps = ListBuffer[Map[String, Any]]()
    val conditions = FileHelper.readFile("./DynConfig/maps/conditions.map")
    maps += Json.parse(conditions).as[Map[String, JsValue]]
  }

  protected def initHBase() {
    val force = false
//    if (! HBase)
  }

  val config = ConfigFactory.load()
  implicit val system = ActorSystem("ESJ-Actors", config.getConfig("AkkaConfig"))

  val dynConfig = ConfigHelper.getConf()
  val number = dynConfig.getString("Actor.ESJ.Number").toInt
  val threshold = dynConfig.getString("Actor.Log.Threshold").toInt
  val interval = dynConfig.getString("Actor.Fq.Interval").toInt
  val syncDelay = 10000
  /***** vars for attempt *****/
  val queue = "Tracks_bash"
  val syncQueue = "Maps_bash"

  val logActor = system.actorOf(Props(classOf[LogActor], threshold), "LogActor")
  val init = system.actorOf(Props(classOf[Init], logActor), "Init")

  object RestartJudge
  case class StartJudge(num: Int)
  object StopJudge
  object LoadMaps
  object SyncMaps

  def main(args: Array[String]) {
    init ! StartJudge(number)
    Thread.sleep(10000)
    init ! RestartJudge

  }
}

class Init(logActor: ActorRef) extends Actor with ActorLogging {

  import Init._
  import services.Actor.LogActor.Info

  context.watch(logActor)
  val name = self.path.toString
  val judges = ListBuffer[ActorRef]()
  var index = 0

  println(s"the path of Init = ${context.self.path}")
  def receive = {
    //    case Terminated(child) =>
    //      println(s"${child.path.name} is dead")
    //      if (context.children.isEmpty) {
    //        println("all children are dead, proceeding to shutdown simulation")
    //        self ! PoisonPill
    //        context.system.shutdown()
    //      }

    case StartJudge(num) =>
      val maps = loadMap
      (0 until num).foreach(i => {
        judges += context.actorOf(Props(classOf[JudgeActor], logActor, maps), s"JudgeActor-$i")
        context.watch(judges.apply(i))
        judges.apply(i) ! PullFq(queue, interval)
      })

    case StopJudge =>
      val n = judges.length
      if (n == 0) logActor ! Info(s"$name: StopJudge:JudgeActors not started or already stopped before")
      else {
        judges.foreach(judge => {
          //judge ! PoisonPill

          judge ! Pause
          context.stop(judge)
        })
        judges.clear
        logActor ! Info(s"$name: StopJudge: success to stop $n JudgeActors")
      }

    case RestartJudge =>
      self ! StopJudge
      Thread.sleep(3000)
      self ! StartJudge(number)

    case SyncMaps =>
      val fqClient = FqueueHelper.client()
      Try(fqClient.pull(syncQueue).get) match {
        case Success(map) =>
          if (FileHelper.save2File("./DynConfig/maps/conditions.map", map) == true)
            logActor ! Info(s"$name: SyncMaps: success to sync maps")
          println(map)
          Thread.sleep(2000)
          self ! RestartJudge

        case Failure(ex: Throwable) =>
          logActor ! Err(s"$name: SyncMaps: $ex")
      }
      Thread.sleep(syncDelay)
      self ! SyncMaps
  }

}
