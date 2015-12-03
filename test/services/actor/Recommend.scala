package services.actor

import common.HBaseHelper.{HBaseHelper, Row}
import services.business.ServingLayer

import scala.collection.mutable.{Map => muMap}
/**
 * Created by horatio on 11/25/15.
 */
object Recommend {

  def rec(matches: Map[String, Map[String, String]], priorities: Map[String, String]): Map[String, Row] ={
    import scala.collection.mutable.{Map => muMap}
    val rows = muMap[String, Row]()
    matches.keys foreach(uid =>{

      val value = matches.get(uid).get
      val templateId = value.get("TemplateId").get
      val sendTime = value.get("SendTime").get
      val mprioritie = priorities.get(templateId).get.toInt
      val rowPull = HBaseHelper.getRow("CWX_table", Iterable(uid)).get(uid).get
      if (rowPull != null) {
        val qav = rowPull.qualifersAndValues
        val tprioritie = qav.get("Prioritie").get.toInt
        if (mprioritie > tprioritie) {
          val family = rowPull.family
          val items = ServingLayer.getItemsByUid(uid, "10")
          val tags = ServingLayer.getTagsByUid(uid, "10")
          val qualifersAndValues = muMap[String, String]()
          qualifersAndValues += ("TemplateId" -> templateId)
          qualifersAndValues += ("SendTime" -> sendTime)
          qualifersAndValues += ("Tags" -> tags)
          qualifersAndValues += ("Items" -> items)
          qualifersAndValues += ("Prioritie" -> mprioritie.toString)
          val row = new Row(uid, family, qualifersAndValues.toMap)
          rows += (uid -> row)
        }
      }
    })
    rows.toMap
  }
  def main(args: Array[String]) {

  }
}
