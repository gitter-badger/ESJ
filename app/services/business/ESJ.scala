package services.business

import common.LogHelper.LogHelper
import play.api.libs.json._

/**
 * Created by horatio on 10/27/15.
 */
object ESJ {

  def judge(record: JsValue) {
    println(Json.prettyPrint(record))


    try {
      val records = record.as[Map[String, JsValue]]
      records.keys.foreach(uid => {
        val tracks = record \ uid
        val act = "v"

        /** extract data from json **/

        /**
         * viewtime means visit time
         **/
        val vTime = (tracks \ "viewtime").as[String]
        val durs = (tracks \ "duration").as[String]
        val pInfos = tracks \ "pageinfo"
        println(pInfos)
      })
    } catch {
      case ex: Exception =>
        LogHelper.err(s"ESJ: judgeScene: ${ex.getMessage()}" + "\n")
    }

  }
}
