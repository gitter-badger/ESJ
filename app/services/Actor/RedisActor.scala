//package services.Actor
//
//import akka.actor.{Actor, ActorLogging}
//
//
///**
// * Created by horatio on 10/27/15.
// */
//object RedisActor {
//  case class Rpush(key: String, msgs: List[String])
//  case class Lpop(key: String, count: Long)
//}
//
//class RedisActor extends Actor with ActorLogging {
//
//  import services.Actor.RedisActor.Rpush
//
////  val rpush = context.actorOf(Props(classOf[RedisHelper]), "Rpush")
//  def receive = {
//
//    case Rpush(key, msgs) =>
//      println(s"rpush $msgs to $key")
//  }
//}