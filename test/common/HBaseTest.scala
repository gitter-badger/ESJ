package common.HBaseHelper

import common.DateHelper.DateHelper

/**
 * Created by horatio on 11/21/15.
 */
object HBaseTest {

  def main(args: Array[String]) {

    val table = "CWX_Test1"
    val family = "test"
    var keys = Iterable("00IxfYPJynhrRWo9", "00W8CeUO32voaJxW", "MLmXNE5kz2ir0DLp", "lE3rSQNrlEMy7GFl", "zz9OR4zaVDfln9wU", "00RQKvBBpw0LdtYE" ,"asbBjLWk5mbKkVQh", "qqqqqqqqppppkkkkgg", "oxq6nXUidewK2qbS", "EZSR2sM1zZLWeuSo")

    println("start  put data  " + DateHelper.getTimeOfDtae2Format)
    HBaseHelper.getRows(table, keys).seq.foreach(e => println(e))
    println("end put data  " + DateHelper.getTimeOfDtae2Format)

    //                println("start  put data  " + DateHelper.getTimeOfDtae2Format)
    //                for (i <- 0 to 10000) {
    //                  val qvs = muMap[String, String]()
    //                  qvs += ("1" -> "1")
    //                  qvs += ("2" -> "2")
    //                  qvs += ("3" -> "3")
    //                  qvs += ("4" -> "4")
    //                  val rd = random(16)
    //                  val ird = Iterable(random(16))
    //                  keys = keys ++ ird
    //                  val row = new Row(rd, family, qvs.toMap)
    //                  val t = setRow(table, row)
    //                  println(s"${i}" +" " + s"${t}")
    //                }
    //                println("end put data  " + DateHelper.getTimeOfDtae2Format)

  }
}


//  def setColumnValue(tableNameStr: String, key: String, family: String, qualifer: String, value: String): Boolean = {
//    if (ensureTableFamily(tableNameStr, family) == false){
//      println("table or family no exist")
//      return false
//    }
//
//    try {
//      val putRequest = new PutRequest(tableNameStr, key, family, qualifer,value)
//      val put = client.atomicCreate(putRequest).join
//
//      true
//    }catch{
//      case ex: Exception => println(s"err: ${ex.getMessage}")
//        false
//    }
//  }
//
//  def setRow(tableNameStr: String, row: Row): Boolean ={
//    if (ensureTable(tableNameStr) == false){
//      println("table no exist")
//      return false
//    }
//
//    try {
//      val key = row.key
//      val family = row.family
//      val qualifersAndValues = row.qualifersAndValues
//
//      qualifersAndValues.foreach(e =>{
//        setColumnValue(tableNameStr,key,family,e._1,e._2)
//      })
//
//      true
//    }catch{
//      case ex: Exception => println(s"err: ${ex.getMessage}")
//        false
//    }
//  }
//
//  def addColumn(tableNameStr: String,key: String, family: String, newQualifer: String, value: String): Boolean = {
//    if (ensureTableFamily(tableNameStr, family) == false){
//      println("table or family no exist")
//      return false
//    }
//
//    try {
//      val putRequest = new PutRequest(tableNameStr, key, family, newQualifer,value)
//      val put = client.put(putRequest).join
//
//      true
//    }catch{
//      case ex: Exception => println(s"err: ${ex.getMessage}")
//        false
//    }
//  }
//

//  def getColumnValue(tableNameStr: String, key: String, family: String, qualifer: String): String = {
//    if (ensureTableFamily(tableNameStr, family) == false){
//      println("table or family no exist")
//      return null
//    }
//
//    try{
//      val getRequest = new GetRequest(tableNameStr, key, family, qualifer)
//      val kv = client.get(getRequest).join
//      val data = new String(kv.get(0).value)
//
//      data
//    }catch{
//      case ex: Exception => println(s"err: ${ex.getMessage}")
//        null
//    }
//  }
//
//  def getColumnAllValues(tableNameStr: String, key: String, family: String,
//  qualifer: String): ArrayBuffer[String] = {
//    if (ensureTableFamily(tableNameStr, family) == false){
//      println("table or family no exist")
//      return null
//    }
//
//    try{
//      val getRequest = new GetRequest(tableNameStr, key, family, qualifer).maxVersions(Integer.MAX_VALUE)
//      val kvs = client.get(getRequest).join
//      val values = new ArrayBuffer[String](kvs.size)
//
//      for (i <- 0 to (kvs.size - 1)){
//        values += new String(kvs.get(i).value())
//      }
//
//      values
//    }catch{
//      case ex: Exception => println(s"err: ${ex.getMessage}")
//        null
//    }
//  }
//
