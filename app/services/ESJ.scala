package services

import business.DataManager.ServingLayer
import common.FileHelper.FileHelper
import common.HbaseHelper.{HbaseColumn, HbaseHelper}
import common.LogHelper.LogHelper
import config.DynConfiguration
import play.api.libs.json._
import play.api.mvc._

import scala.collection.mutable.ArrayBuffer
import scala.util.control.Breaks._

object ESJ extends Controller {
  val DEBUG = true

  def getHbaseValueByConf(tableName: String, rowKey: String, familyName: String,
                          columnName: String): ArrayBuffer[String] = {

    val hbaseConf = FileHelper.readFile_kv("./DynConfig/hbase.conf", "=")
    val table = DynConfiguration.getConf().getString("Business.Name") +  "_" + hbaseConf(tableName)
    val family = hbaseConf(familyName)
    val column = hbaseConf(columnName)

    /**
     * error: data: getColumnAtAllVersion: null
     **/
    val verBuffer = HbaseHelper.getColumnAtAllVersion(table, rowKey, family, column)
    if (!DEBUG) {
      println(s"${rowKey}-----${verBuffer.size}")
      verBuffer.sortWith(_ > _).foreach(println)
    }

    verBuffer
  }

  def getEmailBasics(sceneId: String, viewTime: String): JsObject = {

    /** get email basics, including Scene Id, Priority and Sending Time = present time + Scene Frequecy **/
    val vt = viewTime.toLong
    val priority = sceneId.substring(1, 2)
    var sendTime = 0L

    sceneId match {
      /** first visit, sent 12 hours later **/
      case "T31" => sendTime = vt + 43200
      /** visit invitation, sent 6 hours later **/
      case "T32" => sendTime = vt + 21600
      /** recommendation after viewing, sent 6 hours **/
      case "W23" => sendTime = vt + 21600
      /** info page email, sent 24 hours later **/
      case _ =>
        if (sceneId.substring(0, 2) == "I3") sendTime = vt + 86400
        else throw new IllegalArgumentException(s"getEmailBasics: ${sceneId} not a valid Scene Id")
    }

    val basics = Json.obj(("SceneId", sceneId), ("Priority", priority), ("SendTime", sendTime.toString()))
    if (!DEBUG) println(s"--------- ${sceneId} ---------\n ${Json.prettyPrint(basics)}")

    basics
  }

  def getEmailContent(basics: JsObject, uid: String, pageInfos: JsValue): JsObject = {

    val dynConf = DynConfiguration.getConf()
    val num = dynConf.getString("Email.Item.Number")

    val tablePath = dynConf.getString("Table.Item2Tags")
    val item2Tags = FileHelper.readFile_kv(tablePath, "\t")
    val SceneId = Json.stringify(basics \ "SceneId")

    /** try to query the records of Uid on Hbase. If Null, recommend popular items to Uid **/
    var itemMap = Map[String, Int]()
    if (ServingLayer.getItemInterestsByUid(uid, num).as[Map[String, Int]] != itemMap && SceneId != "T31") {
      itemMap = ServingLayer.getItemInterestsByUid(uid, num).as[Map[String, Int]]
    } else itemMap = ServingLayer.getItemsDistribution(num, false).as[Map[String, Int]]

    val items = ArrayBuffer[String]()
    itemMap.toList.sortWith(_._2 > _._2).foreach(item => items += item._1)

    /** find tags of email, join tags of every items' **/
    val tags = ArrayBuffer[String]()
    itemMap.keys.foreach(key => {
      tags ++= item2Tags.getOrElse(key, "").split(",").toList
    })

    /** get email content = basics + tags + items **/
    val content = basics ++ Json.obj(("Items", items), ("TagCodes", tags.distinct))
    if (DEBUG) LogHelper.infoLoger(s"--------- ${uid}  ---------\n${Json.prettyPrint(content)}")
    content
  }


  def createEmailTask(uid: String, content: JsObject): Boolean = {

    try {
      val data = ArrayBuffer[HbaseColumn]()
      val hbaseConf = FileHelper.readFile_kv("./DynConfig/hbase.conf", "=")
      val dynConf = DynConfiguration.getConf()

      val business = dynConf.getString("Business.Name")
      val redisQueueKey = dynConf.getString("Redis.QueueKey")

      val table = business + "_" + hbaseConf("Scene")
      val family = hbaseConf("Scene.Family")

      var column = hbaseConf("Scene.Priority")
      val priority = HbaseHelper.getColumnAtAllVersion(table, uid, family, column)

      val SceneId = Json.stringify(content \ "SceneId")
      if (priority.size != 0 && SceneId != "T31") {
        val contentMap = content.as[Map[String, String]]

        /** low priority scene triggered but not create email task **/
        if (priority.size != 0 && priority(0).toLong <= contentMap("Priority").toLong) return false
        if (!DEBUG) println(s"---hbase--------${priority(0).toLong}---" + s"Map----${contentMap("Priority").toLong}---- ")

        column = hbaseConf("Scene.SendTime")
        val sendTime = HbaseHelper.getColumnAtAllVersion(table, uid, family, column)
        contentMap.keys.foreach(key => data += HbaseColumn(key, contentMap(key)))
        HbaseHelper.addColumnBatch(table, uid, family, data)
      } else {
//        data += HbaseColumn("SceneId", Json.stringify(content \ "SceneId"))
//        data += HbaseColumn("Priority", Json.stringify(content \ "Priority"))
//        data += HbaseColumn("SendTime", Json.stringify(content \ "SendTime"))
//        data += HbaseColumn("TagCodes", Json.stringify(content \ "TagCodes"))
//        data += HbaseColumn("Items", Json.stringify(content \ "Items"))
//
//        HbaseHelper.addColumnBatch(table, uid, family, data)


      }
      true
    } catch {
      case ex: Exception =>
        LogHelper.errLoger(s"ESJ: createEmailTask: ${ex.getMessage()}" + "\n")
        false
    }
  }

  def judgeScene(json: JsValue): Boolean = {
    if (!DEBUG)    println(s"----judge----${Json.prettyPrint(json)}-------")
    /** load page tags from page tag table  **/

    /** API Secured **/
    try {
      val userTracks = json.as[Map[String, JsValue]]
      userTracks.keys.foreach(uid =>{
        val tracks = json \ uid
        val act = "v"

        /** extract data from json **/

        /**
         *  viewtime means visit time
         **/
        val vTime = (tracks \ "viewtime").as[String]
        val durs = (tracks \ "duration").as[String]
        val pInfos = tracks \ "pageinfo"

        val sceneJuge = new SceneJudge
        val sceneId = sceneJuge.judgeScene(uid, vTime, act, durs, pInfos)

        breakable {
          if (sceneId == "") {
            LogHelper.infoLoger(s"ESJ: judgeScene: ${uid}: no scene triggered" + "\n")
            /** continue the loop **/
            break
          } else {
            val basics = getEmailBasics(sceneId, vTime)
            val content = getEmailContent(basics, uid, pInfos)
            createEmailTask(uid, content)
          }
        }
      })
      true
    } catch {
      case ex: Exception =>
        LogHelper.errLoger(s"ESJ: judgeScene: ${ex.getMessage()}" + "\n")
        false
    }
  }

}

