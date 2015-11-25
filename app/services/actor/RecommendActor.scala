package services.actor

import akka.actor.{Actor, ActorLogging}
import services.actor.RecommendActor.Query

class RecommendActor extends Actor with ActorLogging {
  def receive = {
    case _ =>
    case Query(matches, priorities) =>

  }
}

object RecommendActor {
  case class Query(matches: Map[String, Map[String, String]], priorities: Map[String, String])

}