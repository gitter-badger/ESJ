package services.business

import play.api.libs.json.{JsValue, Json}

import scala.collection.mutable.ListBuffer
import scala.util.control.Breaks._

/**
 * Created by horatio on 10/29/15.
 */
object Scenes {

  val infos = Json.parse("""{"gender": "female", "age": 28, "area": "south", "salary": 7000}""")

  /***** need optimizing *****/
  private def judgeUserInfos(uid: String, code: String, sid: String): String ={
    var tid = sid
    val codes = ListBuffer[String]()
    code map {option => codes += option.toString}

    breakable {
      val gender = UserInfos.judgeGender(infos, codes.apply(0))
      if (gender != codes.apply(0)) {
        tid = sid
        break
      } else tid += gender

      val age = UserInfos.judgeAge(infos, codes.apply(1))
      if (age != codes.apply(1)) {
        tid = sid
        break
      } else tid += age

      val area = UserInfos.judgeArea(infos, codes.apply(2))
      if (area != codes.apply(2)) {
        tid = sid
        break
      } else tid += area

      val salary = UserInfos.judgeSalary(infos, codes.apply(3))
      if (salary != codes.apply(3)) {
        tid = sid
        break
      } else tid += salary
    }

    tid
  }

  def firstVisit(uid: String, tracks: JsValue, triggers: Map[String, JsValue]): String = {
    val sid = "T1-"
    var tid = ""

//    val visit = HBaseHelper.getColumn("hanhou_EmailScene", "005a7c34b3b2e631", "Scene", "Items")
//    println(visit)
    triggers.keys.foreach({ code =>
      val duration = (tracks \ "duration").as[String].toInt
      val Durations = (triggers.get(code).get \ "Durations").as[Int]

      if (duration >= Durations) {
        if (judgeUserInfos(uid, code, sid) == sid + code) tid = sid + code
      }
    })
    tid
  }

  def visitInvitation(uid: String, tracks: JsValue, triggers: Map[String, JsValue]): String = {
    var tid = "T1-"
    triggers.keys map  { code =>
      val duration = (tracks \ "duration").as[String].toInt
      val Durations = (triggers.get(code).get \ "Durations").as[Int]

      if (duration >= Durations) {
        val codes = ListBuffer[String]()
        code map {option => codes += option.toString}
        val gender = UserInfos.judgeGender(infos, codes.apply(0))
        if (gender == codes.apply(0)) tid += gender
        val age = UserInfos.judgeAge(infos, codes.apply(1))
        if (age == codes.apply(0)) tid += age
        tid
      }
    }
    tid
  }

}
