package services.Actor

import akka.actor._
import services.Actor.JudgeActor.PullFq

import scala.collection.mutable.ListBuffer


/**
 * Created by horatio on 10/27/15.
 */
class ESJActor(logActor: ActorRef) extends Actor with ActorLogging {

  import services.Actor.ESJActor.Start

  /***** vars for attempt *****/
  val queue = "Tracks_bash"
  def receive = {

    case Terminated(child) =>
      println(s"${child.path.name} is dead")
      if (context.children.isEmpty) {
        println("all children are dead, proceeding to shutdown simulation")
        self ! PoisonPill
        context.system.shutdown()
      }


    case Start(system, num, interval) =>
      println(s"the path of ESJActor = ${context.self.path}")
      val fqActor = context.actorOf(Props(classOf[FqActor], logActor), "FqActor")
      context.watch(fqActor)

      val judgeActors = ListBuffer[ActorRef]()
      (0 until num).map(i => {
        judgeActors += context.actorOf(Props(classOf[JudgeActor], fqActor, logActor), s"JudgeActor-${i + 1}")
        context.watch(judgeActors.apply(i))
        judgeActors.apply(i) ! PullFq(queue, interval)
        Thread.sleep(2000)
      })

  }

}

object ESJActor {
  case class Start(system: ActorSystem, num: Int, interval: Int)
}