package business.DataManager

import common.FileHelper.FileHelper
import common.HbaseHelper.HbaseHelper
import config.DynConfiguration
import scala.collection.mutable.{ArrayBuffer, Map}

/**
  init HBase tables when service starting
  **/

object HBase {

  private def createTable(table: String, family: String, property: Map[String, String],
                          force: Boolean): Boolean = {

    if (force) return HbaseHelper.createTableForce(table, ArrayBuffer(family), property)
    else return HbaseHelper.createTable(table, ArrayBuffer(family), property)
  }

  def initTable(force:Boolean):Boolean = {

    val dynConf = DynConfiguration.getConf()
    val business = dynConf.getString("Business.Name")

    val hbaseConf = FileHelper.readFile_kv("./DynConfig/hbase.conf", "=")
    var table = business + "_" + hbaseConf("Scene")
    var family = hbaseConf("Scene.Family")
    val property = collection.mutable.Map(
      ("versions", hbaseConf("Hbase.Versions")), ("ttl", hbaseConf("Hbase.TTL")))

    if (!createTable(table, family, property, force)) return false

    table = business + "_" + hbaseConf("Feedback")
    family = hbaseConf("Feedback.Family")
    if (!createTable(table, family, property, force)) return false

    true
  }
}