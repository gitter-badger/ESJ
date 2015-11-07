import akka.actor.{ActorLogging, Actor}

class RecommendActor extends Actor with ActorLogging {
  def receive = {
    case _ =>
  }
}

object RecommendActro {
  case class Query(sceneId: String)

}