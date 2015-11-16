package services.Actor

import akka.actor.{Actor, ActorLogging}

class RecommendActor extends Actor with ActorLogging {
  def receive = {
    case _ =>
  }
}

object RecommendActor {
  case class Query(matches: Map[String, Map[String, String]], priority: Map[String, String])

}