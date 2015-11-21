package services.business

import common.ConfHelper.ConfigHelper
import common.HBaseHelper.{HBaseHelper, Row}
import common.LogHelper.LogHelper
import play.api.libs.json.{JsValue, Json}

import scala.collection.mutable.{Map => muMap}
import scala.util.{Failure, Success, Try}

/**
 * Created by horatio on 10/29/15.
 */
object Scenes {

  val noMatch = Map[String, String]()
  val dynConfig = ConfigHelper.getConf()
  val recordTable = dynConfig.getString("HBase.Table.Record")
  val identityTable = dynConfig.getString("HBase.Table.Identity")

  def judge(record: String, rules: Map[String, Map[String, JsValue]],
            sceneIds: Map[String, String]): Map[String, Map[String, String]] = {

    val matches = muMap[String, Map[String, String]]()
    try {
      /**
       * A Fqueue record converted to a ParMap of several user's track records:
       * Map[Uid, Track] = Map[Uid, Map[PageInfos -> "", Durations -> "", VisitTime -> ""]] =
       * Map[ Uid, Map[Map[Tags, duration], Durations, VisitTime] ]
       */
      val records = Json.parse(record).as[Map[String, JsValue]].par
      val uids = records.keys.seq
      val recordRows = HBaseHelper.getRows(recordTable, uids)
      val identityRows = HBaseHelper.getRows(identityTable, uids)

      uids map { uid =>
        val track = records.get(uid).get
        val identity = identityRows.get(uid)
        recordRows.get(uid) match {
          case Some(recordRow) =>
            var result = visitInvitation(sceneIds.get("VisitInvitation"), rules, track, recordRow, identity)
            if (result == noMatch) {
              result = webTrack(sceneIds.get("WeTrack"), rules, track, identity)
              if (result != noMatch) matches ++= Map(uid -> result)
            } else matches ++= Map(uid -> result)

          case None =>
            /**
             *  Get sceneId for firstVisit and then get the trigger on rules with sceneId
             */
            val result = firstVisit(sceneIds.get("FirstVisit"), rules, track, identity)
            if (result != noMatch) matches ++= Map(uid -> result)
        }
      }
    } catch {
      case ex: Exception =>
        LogHelper.err(s"Scenes: judge: ${ex.getMessage()}")
    }

    matches.toMap
  }


  private def visitInvitation(sceneId: Option[String], rules: Map[String, Map[String, JsValue]],
                              track: JsValue, recordRow: Row, identity: Option[Row]): Map[String, String] = {
    sceneId match {
      case Some (sceneId) =>
        rules.get(sceneId) match {
          case Some(triggers) =>
            triggers.keys foreach { code =>
              val variables = triggers.get(code).get
              val lastTime = recordRow.qualifersAndValues.get("VisitTime").get.toLong
              val visitTime = (track \ "VisitTime").as[String].toLong
              val interval = (variables \ "VisitInterval").as[String].toLong
//              val currentTime = DateHelper.getCurrentTimeSeconds()

              if (lastTime + interval > visitTime) {
                val templateId = sceneId + "-" + code
                val sendTime = (variables \ "SendTime").as[String]
                if (Triggers.judgeVariables(identity, variables, code))
                  return Map("TemplateId" -> templateId, "SendTime" -> sendTime)
              }
            }

          case None =>
        }

      case None =>
    }
    noMatch
  }


  def webTrack(sceneId: Option[String], rules: Map[String, Map[String, JsValue]],
               track: JsValue, identity: Option[Row]): Map[String, String] = {
    sceneId match {
      case Some (sceneId) =>
        rules.get(sceneId) match {
          case Some(triggers) =>
            triggers.keys foreach { code =>
              val variables = triggers.get(code).get

              var action = ""
              Try(track \ "action") match {
                case Success(a) =>
                  action = a.as[String]
                case Failure(ex) =>
                  LogHelper.warn(s"Scenes: judge: : webTrack: ${ex}")
                  action = "v"
              }

            }

          case None =>
        }

      case None =>
    }

    noMatch
  }


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
                if (Triggers.judgeVariables(identity, variables, code))
                  return Map("TemplateId" -> templateId, "SendTime" -> sendTime)
              }
            }

          case None =>
        }

      case None =>
    }
    noMatch
  }


}
