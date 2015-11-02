package services.Actor

import akka.actor.{Actor, ActorLogging, ActorRef}
import common.FqueueHelper.FqueueHelper
import play.api.libs.json._
import services.Actor.JudgeActor.{Pause, Judge, PullFq}
import services.Actor.LogActor.Err
import services.business.ESJ

import scala.collection.mutable.ListBuffer
import scala.util.{Failure, Success, Try}

/**
 * Created by horatio on 10/27/15.
 */
class JudgeActor(logActor: ActorRef, maps: ListBuffer[Map[String, Any]]) extends Actor with ActorLogging {

  val name = self.path.toString
  def receive = {
    case PullFq(queue, time) =>
      val fqClient = FqueueHelper.client()
      Try(fqClient.pull(queue).get) match {
        case Success(msgs) =>
          val record = Json.parse(msgs.toString)
          self ! Judge(record, queue, time)

        case Failure(ex: Throwable) =>
          logActor ! Err(s"$name: PullFq: $ex")
          Thread.sleep(time)
          self ! PullFq(queue, time)
      }

    case Judge(record, queue, time) =>
      val conditions = maps.apply(0).asInstanceOf[Map[String, JsValue]]

      Try(ESJ.judge(record, conditions)) match {
        case Success(ok) =>
        case Failure(ex: Throwable) =>
          logActor ! Err(s"$name: Judge: $ex")
      }
      Thread.sleep(5000)
      self ! PullFq(queue, time)

    case Pause =>
      Thread.sleep(5000)
  }
}

object JudgeActor {
  case class PullFq(queue: String, time: Int)
  case class Judge(record: JsValue, queue: String, interval: Int)
  object Pause
}