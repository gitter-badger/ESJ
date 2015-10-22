package services

import play.api.libs.json._
import scala.collection.mutable.ArrayBuffer
import scala.util.control.Breaks._

/** save email scene data to HBase while put them to email queue **/
class SceneJudge {
  val DEBUG = true

  def judgeFirstVisit(durs: Long, pInfos: Map[String, Map[String, String]],
                      page: String): String = {
    var sceneId = ""

    /** view front page at firt time visit **/
    if (page.substring(0, 7) == "<front>") {
      val dur = pInfos(page).getOrElse("dur", "0").toLong
      /** total duration: 10 min, front page duration: 30 sec to 5 min **/
      if ((durs > 600) || (dur > 30 && dur < 300)) sceneId = "T31"
    }

    sceneId
  }

  def judgeInfoPages(pInfos: Map[String, Map[String, String]], page: String): String = {
    var sceneId = ""
    val dur = pInfos(page).getOrElse("dur", "0").toLong

    if (dur > 30 && dur < 300) {

      if (page == "shoptessure") sceneId = "I31"
      else if (page == "sunscreener") sceneId = "I32"
      else if (page == "facemasque") sceneId = "I33"
      else if (page == "memberright") sceneId = "I34"
      else if (page ==  "brandculture") sceneId = "I35"
    }

    sceneId
  }

  def judgeVisitInvitation(durs: Long, visitTime: String,
                           vtBuffer: ArrayBuffer[String]): String = {

    val vTime = visitTime.toLong
    var vtArray = ArrayBuffer[Long]()
    vtBuffer.foreach(vt => vtArray += vt.toLong)

    val vtSorted = vtArray.sortWith(_ > _)
    if (!DEBUG) vtSorted.foreach(println)

    var sceneId = ""
    for (t <- vtSorted) {
      if (t < vTime) {
        /** 15 day, visit invitation **/
        if ((vTime - t) > 1296000) sceneId = "T32"
      }
    }
    sceneId
  }

  def judgeBehavior(durs: Long, action: String, pInfos: Map[String, Map[String, String]]): String = {

    var sceneId = ""
    /** 15 min, view **/
    if (action == "v") {
      if (durs > 900) sceneId = "W23"
    }

    sceneId
  }

  def judgeScene(uid: String, visitTime: String, action: String, duration: String,
                 pageInfos: JsValue): String = {

    val durs = duration.toLong
    val vtBuffer = ESJ.getHbaseValueByConf("Track", uid, "Track.Family", "Track.VisitTime")
    val pInfos = pageInfos.as[Map[String, Map[String, String]]]
    if (!DEBUG) pInfos.foreach(println)

    var sceneId = ""

    /** judge Scenes and compare present time - last trigger time  and frequency **/
    breakable {
      pInfos.keys.foreach(page => {
        /** judge Info Pages Scenes **/
        sceneId = judgeInfoPages(pInfos, page)
        if (sceneId != "") break

        sceneId = judgeBehavior(durs, action, pInfos)
        if (sceneId != "") break

        /** judge First Visit Scenes or Visit Invitation **/
        if (vtBuffer.size == 0) sceneId = judgeFirstVisit(durs, pInfos, page)
        else sceneId = judgeVisitInvitation(durs, visitTime, vtBuffer)
        if (sceneId != "") break
      })
    }

    if (!DEBUG) println(s"${uid}--------- scendId --------- ${sceneId}")
    sceneId
  }
}