package business.Actor

import akka.actor.Actor
import akka.event.Logging
import common.CaseClass._
import common.FqueueHelper.FqueueHelper
import common.LogHelper.LogHelper
import config.DynConfiguration
import play.api.libs.json._
import services.ESJ

class ESJActor extends Actor {
  val DEBUG = true
  val log = Logging(context.system, this);
  var tracks = Json.parse("""{}""")

  private def getTracksFromFQueue(queue: String): Boolean = {
    try {
      var stop = false
      val fqueue = FqueueHelper.getFqueue()
//      val raw = FileHelper.readFile("/home/horatio/big-data/track_json/T32.json")
//      fqueue.sendQueue(queue, raw)

      while (!stop) {
        val data = fqueue.getQueue(queue)
        data match {
          case None => stop = true
          case Some(json) =>
            tracks = Json.parse(json)
            if (DEBUG)   LogHelper.infoLoger(s"---get FQ-----${Json.prettyPrint(tracks)}-------")
            if (tracks != Json.parse("""{}""")) ESJ.judgeScene(tracks)
          }
        }
      true
    } catch {
      case ex: Exception =>
        LogHelper.errLoger(s"ESJActor: getTracksFromFQueue: ${ex.getMessage}\n")
        false
    }
  }

  def receive = {
    case Work(queue) =>
      try {
        val interval = DynConfiguration.getConf().getString("Actor.ESJ.GetTracksInterval").toLong
        while (true) {
          getTracksFromFQueue(queue)
          Thread.sleep(interval)
          LogHelper.infoLoger(s"${interval.toInt / 1000}s passed--------")
        }
      } catch {
        case ex: Exception =>
          LogHelper.errLoger(s"ESJActor: ${ex.getMessage}\n")
      }

    case _ => log.info("ESJActor: invalid message")
  }

}
