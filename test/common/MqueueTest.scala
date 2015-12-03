package common

/**
 * Created by cwx on 15-11-28.
 */
object MqueueTest {
  def main(args: Array[String]) {
    import scala.collection.mutable.{Map => muMap}
    val uid = "123"
    val qualifersAndValues = muMap[String, String]()
    qualifersAndValues += ("TemplateId" -> "T1")
    qualifersAndValues += ("SendTime" -> "10")
    qualifersAndValues += ("Tags" -> "~")
    qualifersAndValues += ("Items" -> "~")
    qualifersAndValues += ("Prioritie" -> "2")
    val jsStr = s"""{ "uid":"${uid}", "TemplateId":"${qualifersAndValues("TemplateId")}", "SendTime": "${qualifersAndValues("SendTime")}", "Tags": "${qualifersAndValues("Tags")}", "Items": "${qualifersAndValues("Items")}", "Prioritie": "${qualifersAndValues("Prioritie")}"}"""
    val mqueue = MQHelper.MQHelper.getMqueue()
    mqueue.sendQueue("test1", jsStr)
    println("OK")
    val rqueue = mqueue.receiveQueue("test1")
    println(rqueue)
    println(rqueue.receive())
    println("OK")
    mqueue.close()
  }
}
