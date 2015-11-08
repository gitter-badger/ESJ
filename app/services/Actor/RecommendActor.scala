package services.Actor

import akka.actor.{ActorLogging, Actor}

class RecommendActor extends Actor with ActorLogging {
  def receive = {
    case _ =>
  }
}

object RecommendActor {
  case class Query(sceneId: String, scenes: Map[String, String])

}