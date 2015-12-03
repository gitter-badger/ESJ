//package services.actor
//
//import common.HBaseHelper.{HBaseHelper, Row}
//
///**
// * Created by cwx on 15-11-28.
// */
//object SetRow {
//  def main(args: Array[String]) {
//    import scala.collection.mutable.{Map => muMap}
//    val qualifersAndValues = muMap[String, String]()
//    qualifersAndValues += ("TemplateId" -> "W1")
//    qualifersAndValues += ("SendTime" -> "100")
//    qualifersAndValues += ("Tags" -> "tags1")
//    qualifersAndValues += ("Items" -> "items1")
//    qualifersAndValues += ("Prioritie" -> "2")
//    val row = new Row("81a04464c7b4a5ed", "cwx", qualifersAndValues.toMap)
//    HBaseHelper.setRow("CWX_table", row)
//    val qualifersAndValues1 = muMap[String, String]()
//    qualifersAndValues1 += ("TemplateId" -> "W1")
//    qualifersAndValues1 += ("SendTime" -> "100")
//    qualifersAndValues1 += ("Tags" -> "tags2")
//    qualifersAndValues1 += ("Items" -> "items2")
//    qualifersAndValues1 += ("Prioritie" -> "2")
//    val row1 = new Row("3309d2ee4369b2e7", "cwx", qualifersAndValues1.toMap)
//    HBaseHelper.setRow("CWX_table", row1)
//    val qualifersAndValues2 = muMap[String, String]()
//    qualifersAndValues2 += ("TemplateId" -> "W1")
//    qualifersAndValues2 += ("SendTime" -> "100")
//    qualifersAndValues2 += ("Tags" -> "tags3")
//    qualifersAndValues2 += ("Items" -> "items3")
//    qualifersAndValues2 += ("Prioritie" -> "2")
//    val row2 = new Row("dde6a4dfb83bf8e3", "cwx", qualifersAndValues2.toMap)
//    HBaseHelper.setRow("CWX_table", row2)
//  }
//}
