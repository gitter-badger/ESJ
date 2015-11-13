package services.business

import akka.actor.ActorSelection
import common.HBaseHelper.HBaseHelper
import common.HBaseHelper.Row
import common.LogHelper.LogHelper
import play.api.libs.json.{JsValue, Json}

/**
 * Created by horatio on 10/29/15.
 */
object Scenes {
  val infos = Json.parse("""{"gender": "female", "age": 28, "area": "south", "salary": 7000}""")

  def judge(record: String, rules: Map[String, Map[String, JsValue]], logActor: ActorSelection): String = {
    try {
      val T1Rule = rules.get("T1").get
      val T2Rule = rules.get("T2").get
      val I1Rule = rules.get("I1").get

      /**
       * A Fqueue record converted to a ParMap of several user's track records:
       * Map[Uid, Track] = Map[ Uid, Map[PageInfos, Durations, VisitTime] ] =
       * Map[ Uid, Map[Map[Tags, duration], Durations, VisitTime] ]
       */
      val records = Json.parse(record).as[Map[String, JsValue]].par
      val uids = records.keys
      val table = ""
      val recordRows = HBaseHelper.getRows(table, uids)
      val IdentityRows = HBaseHelper.getRows(table, uids)
      val act = "v"

      uids map { uid =>
        val track = records.get(uid).get
        val identity = IdentityRows.get(uid)
        recordRows.get(uid) match {
          case Some(recordRow) =>
//            visitInvitation(uid, track, rules.get("T2"))
          case None =>
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
      case Some(trigger) =>

        /**
         * ParIterable
         */
        trigger.keys.par foreach { code =>
          val variables = trigger.get(code).get
          val duration = (track \ "duration").as[Int]
          val Durations = (variables \ "Durations").as[Int]

          if (duration >= Durations) {
            identity match {
              case Some(row) =>
                val feature = row.qualifersAndValues
                if (Identity.judgeFeature(feature, code, variables)) tid +=code
              case None => tid += code
            }

          }
        }
      case None =>
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
