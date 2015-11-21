//package common.HBaseHelper
//
//import common.ConfHelper.ConfigHelper
//
//import scala.collection.JavaConversions._
//import scala.collection.mutable.Map
//import scala.collection.mutable.ArrayBuffer
//
//import org.apache.hadoop.hbase.util.Bytes
//import org.apache.hadoop.conf.Configuration
//import org.apache.hadoop.hbase.{HColumnDescriptor, HTableDescriptor, TableName, HBaseConfiguration}
//import org.apache.hadoop.hbase.client._
//import org.apache.hadoop.hbase.client.Connection
//import org.apache.hadoop.hbase.client.ResultScanner
//import org.apache.hadoop.hbase.CellUtil
//
//import common.LogHelper.LogHelper
//import common.FileHelper.FileHelper
//
///**
// * Created by horatio on 11/17/15.
// */
//object HBaseTest {
//
//  val conn = getHbaseConnection();
//  val admin = conn.getAdmin;
//
//  def getHbaseConfig(conf_kv: Map[String, String]): Configuration = {
//    val HConf = HBaseConfiguration.create();
//
//    conf_kv.keys.foreach { cmd =>
//      HConf.set(cmd, conf_kv(cmd));
//    }
//
//    return HConf;
//  }
//
//  def getHbaseConnection(): Connection = {
//    val conf_kv = ConfigHelper.getMap("./DynConfig/hbase.conf", "=");
//    ConnectionFactory.createConnection(getHbaseConfig(conf_kv));
//  }
//
//  private def setHColumnDescriptor(hc: HColumnDescriptor, kv: Map[String, String]): Unit = {
//    if (kv == null) return;
//    else {
//      kv.keys.foreach { key =>
//        val value = kv(key);
//        key match {
//          case "versions" => hc.setMaxVersions(value.toInt);
//          case "ttl" => hc.setTimeToLive(value.toInt);
//        }
//      }
//    }
//  }
//  def createTable(tableName: String, familys: ArrayBuffer[String], kv: Map[String, String]): Boolean = {
//    try {
//      val table = TableName.valueOf(tableName);
//      if (admin.tableExists(table)) {
//        LogHelper.warn(s"the table: ${tableName} was exist\n");
//        return true;
//      }
//
//      val tableDescr = new HTableDescriptor(table);
//
//      familys.foreach { f =>
//        val hc = new HColumnDescriptor(f.getBytes);
//        setHColumnDescriptor(hc, kv);
//        tableDescr.addFamily(hc);
//      }
//
//      admin.createTable(tableDescr);
//
//      return true;
//    } catch {
//      case ex: Exception =>
//        LogHelper.err(s"HbaseHelper err at createTable, ${ex.getMessage}\n");
//        return false;
//    }
//  }
//
//  def main(args: Array[String]) {
//
//
//  }
//
//}
