package services.Actor

import akka.actor.{Actor, ActorLogging, ActorRef}
import play.api.libs.json.JsValue
import services.Actor.FqActor.Pull
import services.Actor.JudgeActor.{PullFq, Work}
import services.Actor.LogActor.Err
import services.business.ESJ

import scala.util.{Failure, Success, Try}

/**
 * Created by horatio on 10/27/15.
 */
class JudgeActor(fqActor: ActorRef, logActor: ActorRef) extends Actor with ActorLogging {

  def receive = {
    case PullFq(queue, interval) =>
      fqActor ! Pull(queue, interval)

    case Work(record, queue, interval) =>
      Try(ESJ.judge(record)) match {
        case Success(ok) =>
          self ! PullFq(queue, interval)
        case Failure(ex: Throwable) =>
          logActor ! Err(s"${self.path.toString}: $ex")
      }

  }
}

object JudgeActor {
  case class PullFq(queue: String, interval: Int)
  case class Work(record: JsValue, queue: String, interval: Int)
}