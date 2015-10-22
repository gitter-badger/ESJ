package business.Actor

import akka.actor.Actor
import akka.event.Logging
import play.api.libs.json._
import common.FqueueHelper.FqueueHelper
import common.LogHelper.LogHelper
import config.DynConfiguration
import common.CaseClass._


class SyncTableActor extends Actor {

  val log = Logging(context.system, SyncTableActor.this)
  val dynConf = DynConfiguration.getConf()
  val rootDir = dynConf.getString("Table.RootDir")

  /** sync tables from FQueue every day **/
  private def getTableFromFQueue(FQName: String): String = {
    var table = Unit.toString()
    try {
      var stop = false
      val fqueue = FqueueHelper.getFqueue();
      while (!stop) {
        val data = fqueue.getQueue(FQName)
        data match {
          case None => stop = true
          case Some(line) => table = line
        }
      }
      return table
    } catch {
      case ex: Exception =>
        LogHelper.errLoger(s"SycnTableActor: getTableFromFQueue: ${ex.getMessage}\n")
        return Unit.toString()
    }
  }

  private def getSceneFreqTable(): Boolean = {

    true
  }

  def resovleTable(data: String): Boolean = {
    val json = Json.parse(data)
    val table = json.as[Map[String, String]]

    table.keys.foreach { key =>
      if (key == "SceneFreq") getSceneFreqTable

    }
    true
  }

  def receive = {
    case Work(info) => {
      Thread.sleep(3000)
      try {
        val data = getTableFromFQueue("codemap")
        if (data != Unit.toString()) {
          if (resovleTable(data)) {

          }
        }
      } catch {
        case ex:Exception =>
          LogHelper.errLoger(s"SyncTable: ${ex.getMessage}\n")
      }
    }
    case _ => log.info("SyncTable: invalid message")
  }
}
