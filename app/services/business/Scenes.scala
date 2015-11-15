package services.business

import akka.actor.ActorSelection
import common.HBaseHelper.HBaseHelper
import common.HBaseHelper.Row
import common.LogHelper.LogHelper
import play.api.libs.json.{JsObject, JsValue, Json}

/**
 * Created by horatio on 10/29/15.
 */
object Scenes {

  def judge(record: String, rules: Map[String, Map[String, JsValue]], logActor: ActorSelection): String = {
    try {

      /**
       * A Fqueue record converted to a ParMap of several user's track records:
       * Map[Uid, Track] = Map[ Uid, Map[PageInfos, Durations, VisitTime] ] =
       * Map[ Uid, Map[Map[Tags, duration], Durations, VisitTime] ]
       */
      val records = Json.parse(record).as[Map[String, JsValue]].par
      val uids = records.keys
      val table = ""
      val recordRows = HBaseHelper.getRows(table, uids)
      val identityRows = HBaseHelper.getRows(table, uids)

      uids map { uid =>
        val track = records.get(uid).get
        val identity = identityRows.get(uid)
        recordRows.get(uid) match {
          case Some(recordRow) =>
//            visitInvitation(uid, track, rules.get("T2"))
          case None =>
            /** T1 SceneId for firstVisit **/
            firstVisit(track, identity, rules.get("T1"))
        }
      }
    } catch {
      case ex: Exception =>
        LogHelper.err(s"ESJ: judge: ${ex.getMessage()}" + "\n")
    }
    ""
  }


  def firstVisit(track: JsValue, identity: Option[Row], rule: Option[Map[String, JsValue]]): String = {
    var tid = "T1-"

    rule match {
      case Some(triggers) =>
        /**
         * ParIterable
         */
        triggers.keys.par foreach { code =>
          val variables = triggers.get(code).get
          val durs = (track \ "duration").as[String].toInt
          val durations = (variables \ "Durations").as[Int]

          if (durs < durations) {
            identity match {
              case Some(row) =>
                val features = row.qualifersAndValues
                val featureVariables = (variables.as[JsObject] - "Durations" - "SendTime").as[Map[String, String]]
                if (Triggers.judgeVariables(code, featureVariables, features)) tid +=code
              case None =>
                /**
                 * TYPICALLY, assume those without identity information pass and always match the first code!!!
                  */
                tid = tid + code
            }
          } else return tid
        }

      case None => tid = ""
    }

    tid
  }


  def visitInvitation {

  }

  //  private def judgeUserInfos(uid: String, code: String, sid: String): String ={
  //    var tid = sid
  //    val codes = ListBuffer[String]()
  //    code map {option => codes += option.toString}

  //    breakable {
  //      val gender = UserInfos.judgeGender(infos, codes.apply(0))
  //      if (gender != codes.apply(0)) {
  //        tid = sid
  //        break
  //      } else tid += gender
  //
  //      val age = UserInfos.judgeAge(infos, codes.apply(1))
  //      if (age != codes.apply(1)) {
  //        tid = sid
  //        break
  //      } else tid += age
  //
  //      val area = UserInfos.judgeArea(infos, codes.apply(2))
  //      if (area != codes.apply(2)) {
  //        tid = sid
  //        break
  //      } else tid += area
  //
  //      val salary = UserInfos.judgeSalary(infos, codes.apply(3))
  //      if (salary != codes.apply(3)) {
  //        tid = sid
  //        break
  //      } else tid += salary
  //    }
  //
  //    tid
  //  }


}
