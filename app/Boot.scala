/**
 * Created by horatio on 10/27/15.
 */
import akka.actor.{Props, ActorSystem}
import com.typesafe.config.ConfigFactory
import common.ConfHelper.ConfigHelper
import services.Actor.{LogActor, ESJActor}
import services.Actor.ESJActor.Start

object Boot extends App {

  val config = ConfigFactory.load()
  implicit val system = ActorSystem("ESJ-Actors", config.getConfig("AkkaConfig"))

  val dynConfig = ConfigHelper.getConf()
  val num = dynConfig.getString("Actor.ESJ.Number").toInt
  val threshold = dynConfig.getString("Actor.Log.Threshold").toInt
  val interval = dynConfig.getString("Actor.Fq.Interval").toInt

  val logActor = system.actorOf(Props(classOf[LogActor], threshold), "LogActor")
  val esjActor = system.actorOf(Props(classOf[ESJActor], logActor), "ESJActor")
  esjActor ! Start(system, num, interval)
}
