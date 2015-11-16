package services.business

import common.ConfHelper.ConfigHelper
import common.HBaseHelper.{HBaseHelper, Row}
import common.LogHelper.LogHelper
import play.api.libs.json.{JsObject, JsValue, Json}

import scala.collection.mutable.{Map => muMap}
import scala.concurrent.Future

/**
 * Created by horatio on 10/29/15.
 */
object Scenes {

  val dynConfig = ConfigHelper.getConf()
  val recordTable = dynConfig.getString("HBase.Table.Record")
  val identityTable = ""

  def judge(record: String, rules: Map[String, Map[String, JsValue]],
            sceneIds: Map[String, String]): Map[String, Map[String, String]] = {

    val matches = muMap[String, Map[String, String]]()
    try {
      /**
       * A Fqueue record converted to a ParMap of several user's track records:
       * Map[Uid, Track] = Map[Uid, Map[PageInfos, Durations, VisitTime]] =
       * Map[ Uid, Map[Map[Tags, duration], Durations, VisitTime] ]
       */
      val records = Json.parse(record).as[Map[String, JsValue]].par
      val uids = records.keys

      val recordRows = HBaseHelper.getRows(recordTable, uids)
      val identityRows = HBaseHelper.getRows(identityTable, uids)
      val fu = Future{HBaseHelper.getRows(recordTable, uids)}


      uids map { uid =>
        val track = records.get(uid).get
        val identity = identityRows.get(uid)
        recordRows.get(uid) match {
          case Some(recordRow) =>
          //            visitInvitation(uid, track, rules.get("T2"))

          case None =>
            /** T1 SceneId for firstVisit **/
            val matched = firstVisit(sceneIds.get("FirstVisit"), rules, track, identity)
            matches ++= Map(uid -> matched)
        }
      }
    } catch {
      case ex: Exception =>
        LogHelper.err(s"ESJ: judge: ${ex.getMessage()}" + "\n")
    }

    matches.toMap
  }


  val noMatch = Map[String, String]()


  private def firstVisit(sceneId: Option[String], rules: Map[String, Map[String, JsValue]],
                 track: JsValue, identity: Option[Row]): Map[String, String] = {
    sceneId match {
      case Some (sceneId) =>
        rules.get(sceneId) match {
          case Some(triggers) =>
            /**
             * ParIterable
             */
            triggers.keys foreach { code =>
              val variables = triggers.get(code).get
              val durs = (track \ "duration").as[String].toInt
              val durations = (variables \ "Durations").as[String].toInt

              if (durs < durations) {
                val templateId = sceneId + "-" + code
                val sendTime = (variables \ "SendTime").as[String]
                identity match {
                  case Some(row) =>
                    val features = row.qualifersAndValues
                    val featureVariables = (variables.as[JsObject] - "Durations" - "SendTime").as[Map[String, String]]
                    if (Triggers.judgeVariables(code, featureVariables, features)) {
                      return Map("TemplateId" -> templateId, "SendTime" -> sendTime)
                    }
                  case None =>
                    /**
                     * TYPICALLY, assume those without identity information pass and always match the first code!!!
                     */
                    return Map("TemplateId" -> templateId, "SendTime" -> sendTime)
                }
              }
            }

          case None =>
        }
      case None =>
    }
    noMatch
  }


  def visitInvitation {

  }

}
