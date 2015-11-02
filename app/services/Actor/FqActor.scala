//package services.Actor
//
//import akka.actor.{Actor, ActorLogging, ActorRef}
//import common.FqueueHelper.FqueueHelper
//import services.Actor.FqActor._
//import services.Actor.JudgeActor.PullFq
//import services.Actor.LogActor.Err
//
//import scala.util.{Failure, Success, Try}
//
//
///**
// * Created by horatio on 10/27/15.
// */
//class FqActor(logActor: ActorRef) extends Actor with ActorLogging {
//
//
//  val fqCli = FqueueHelper.client()
//  val msg = """{"OS":"Linux"}"""
//  def receive = {
//
//    case Pull(queue, interval) =>
//      Try(fqCli.pull(queue).get) match {
//        case Success(msgs) =>
//          println(msgs)
//          val record = Json.parse(msgs.toString)
//          sender ! Work(record, queue, interval)
//
//        case Failure(ex: Throwable) =>
//          logActor ! Err(s"${self.path.toString}: $ex")
//          /**************/
//          Thread.sleep(interval)
//          sender ! PullFq(queue, interval)
//      }
//
//    case Stop =>
//      fqCli.close()
//  }
//}
//
//object FqActor {
//  case class Pull(queue: String, interval: Int)
//  object Stop
//}