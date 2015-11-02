package services.business

import common.LogHelper.LogHelper
import play.api.libs.json._

/**
 * Created by horatio on 10/27/15.
 */
object ESJ {

  protected def loadConditions(conditions: Map[String, JsValue], sceneId: String): Map[String, JsValue] = {

    import scala.collection.mutable.{Map => mMap}
    val buffer = mMap[String, JsValue]()
    conditions.keys map { tid =>
      tid.substring(0, 2) match {
        case `sceneId` =>
          val code = tid.substring(3)
          val triggers = conditions.get(tid).get
          buffer += code -> triggers
        case _ =>
      }
    }
    buffer.toMap
  }

//  def main(args: Array[String]) {
//    val record = Json.parse(FileHelper.readFile("/home/horatio/big-data/track_json/record.json"))
//    judge(record)
//  }

  def judge(record: JsValue, conditions: Map[String, JsValue]) {

    try {
      val records = record.as[Map[String, JsValue]]
      records.keys map { uid =>
        val tracks = record \ uid
        val act = "v"

        val t1Triggers = loadConditions(conditions, "T1")
        val tid = Scenes.firstVisit(uid, tracks, t1Triggers)
        println(s"$tid ---- ")
//        /** extract data from json **/
//        /**
//         * viewtime means visit time
//         **/
//        val vTime = (tracks \ "viewtime").as[String]
//        val durs = (tracks \ "duration").as[String]
//        val pInfos = tracks \ "pageinfo"
//        println(pInfos)
      }
    } catch {
      case ex: Exception =>
        LogHelper.err(s"ESJ: judge: ${ex.getMessage()}" + "\n")
    }

  }
}
