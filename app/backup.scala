///**
// * Created by horatio on 10/27/15.
// */
//
//import akka.actor._
//import com.typesafe.config.ConfigFactory
//import common.ConfHelper.ConfigHelper
//import common.FileHelper.FileHelper
//import common.FqueueHelper.FqueueHelper
//import play.api.libs.json.{JsValue, Json}
//import services.actor.SceneActor.{Pause, PullFq}
//import services.actor.LogActor.Err
//import services.actor.{SceneActor, LogActor}
//
//import scala.collection.mutable.ListBuffer
//import scala.util.{Failure, Success, Try}
//
//object Init {
//
//  protected def loadRules: ListBuffer[Map[String, Any]] = {
//    val maps = ListBuffer[Map[String, Any]]()
//    val conditions = FileHelper.readFile("./DynConfig/maps/rules.map")
//    maps += Json.parse(conditions).as[Map[String, JsValue]]
//  }
//
//  protected def initHBase() {
//    val force = false
////    if (! HBase)
//  }
//
//  val config = ConfigFactory.load()
//  implicit val system = ActorSystem("ESJ-Actors", config.getConfig("AkkaConfig"))
//
//  val dynConfig = ConfigHelper.getMap()
//  val number = dynConfig.getString("actor.ESJ.Number").toInt
//  val threshold = dynConfig.getString("actor.Log.Threshold").toInt
//  val interval = dynConfig.getString("actor.Fq.Interval").toInt
//  val syncDelay = 10000
//  /***** vars for attempt *****/
//  val queue = "Tracks_bash"
//  val syncQueue = "Maps_bash"
//
//  val logActor = system.actorOf(Props(classOf[LogActor], threshold), "LogActor")
//  val init = system.actorOf(Props(classOf[Init], logActor), "Init")
//
//  object RestartJudge
//  case class StartJudge(num: Int)
//  object StopJudge
//  object LoadMap
//  object SyncMap
//
//  def main(args: Array[String]) {
//    init ! StartJudge(number)
//    Thread.sleep(10000)
//    init ! RestartJudge
//
//  }
//}
//
//class Init(logActor: ActorRef) extends actor with ActorLogging {
//
//  import Init._
//  import services.actor.LogActor.Info
//
//  context.watch(logActor)
//  val name = self.path.toString
//  val judges = ListBuffer[ActorRef]()
//  var index = 0
//
//  println(s"the path of Init = ${context.self.path}")
//  def receive = {
//    //    case Terminated(child) =>
//    //      println(s"${child.path.name} is dead")
//    //      if (context.children.isEmpty) {
//    //        println("all children are dead, proceeding to shutdown simulation")
//    //        self ! PoisonPill
//    //        context.system.shutdown()
//    //      }
//
//    case StartJudge(num) =>
//      val maps = loadRules
//      (0 until num).foreach(i => {
//        judges += context.actorOf(Props(classOf[SceneActor], logActor, maps), s"SceneActor-$i")
//        context.watch(judges.apply(i))
//        judges.apply(i) ! PullFq(queue, interval)
//      })
//
//    case StopJudge =>
//      val n = judges.length
//      if (n == 0) logActor ! Info(s"$name: StopJudge:JudgeActors not started or already stopped before")
//      else {
//        judges.foreach(judge => {
//          //judge ! PoisonPill
//
//          judge ! Pause
//          context.stop(judge)
//        })
//        judges.clear
//        logActor ! Info(s"$name: StopJudge: success to stop $n JudgeActors")
//      }
//
//    case RestartJudge =>
//      self ! StopJudge
//      Thread.sleep(3000)
//      self ! StartJudge(number)
//
//    case SyncMap =>
//      val fqClient = FqueueHelper.client()
//      Try(fqClient.pull(syncQueue).get) match {
//        case Success(map) =>
//          if (FileHelper.save2File("./DynConfig/maps/rules.map", map) == true)
//            logActor ! Info(s"$name: SyncMap: success to sync maps")
//          println(map)
//          Thread.sleep(2000)
//          self ! RestartJudge
//
//        case Failure(ex: Throwable) =>
//          logActor ! Err(s"$name: SyncMap: $ex")
//      }
//      Thread.sleep(syncDelay)
//      self ! SyncMap
//  }
//
//}
