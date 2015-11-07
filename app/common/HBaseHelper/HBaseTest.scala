package common.HBaseHelper

import org.hbase.async.{KeyValue, Config, GetRequest, HBaseClient}

import scala.util.{Failure, Success, Try}


/**
 * Created by horatio on 10/30/15.
 */
object HBaseTest {

  val config = new Config("./DynConfig/AsyncHBase.conf")
  val client = new HBaseClient(config)

  def getData(tableName: String, rowKey: String): KeyValue = {

    val getRequest = new GetRequest(tableName, rowKey)
    val kvs = client.get(getRequest).join
    val data = kvs.get(0)
    data
  }

  def main(args: Array[String]) {
    Try(client.ensureTableExists("fsad")) match {
      case Success(ok) => println("ok")
      case Failure(ex: Throwable) => println(s"err: ${ex}")
    }

    val key = "005a7c34b3b2e631"
    val table = "hanhou_EmailScene"
    val data = getData(table, key)

    val rowKey = new String(data.key)
    println(s"--------${data}------------")

  }


}
