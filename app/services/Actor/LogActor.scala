package services.Actor

import akka.actor.Actor
import common.LogHelper.LogHelper
import services.Actor.LogActor.{Info, Err, Warn}

import scala.collection.mutable.ListBuffer

/**
 * Created by horatio on 10/28/15.
 */
class LogActor(threshold: Int) extends Actor {

  val errBuffer = ListBuffer[String]()
  val warnBuffer = ListBuffer[String]()
  val infoBuffer = ListBuffer[String]()
  def receive = {
    case Err(msgs) =>
      errBuffer += msgs
      if (errBuffer.length >= threshold) {
          errBuffer.foreach(log => LogHelper.err(log))
      }

    case Warn(msgs) =>
      warnBuffer += msgs
      if (warnBuffer.length >= threshold) {
        warnBuffer.foreach(log => LogHelper.warn(log))
      }

    case Info(msgs) =>
      infoBuffer += msgs
      if (infoBuffer.length >= threshold) {
        infoBuffer.foreach(log => LogHelper.info(log))
      }
  }
}

object LogActor {
  case class Err(msgs: String)
  case class Warn(msgs: String)
  case class Info(msgs: String)
  case class Debug(msgs: String)
  case class Write(buf: ListBuffer[String])
}