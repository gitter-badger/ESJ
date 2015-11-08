//package common.HBaseHelper
///*
// * Copyright (c) 2015, BoDao, Inc. All Rights Reserved.
// *
// * Author@ dgl
// *
// * This software is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
// * CONDITIONS OF ANY KIND, either express or implied.
// *
// * you can operate hbase(1.1.1) Conveniently by this software.
// *
// * connect hbase
// * create hbase table
// * add Column to hbase table
// * get Column in hbase table
// * get Column's All Versions data in hbase table
// *
// */
//
//import common.FileHelper.FileHelper
//import common.LogHelper.LogHelper
//
//import org.apache.hadoop.conf.Configuration
//import org.apache.hadoop.hbase._
//import org.apache.hadoop.hbase.client._
//import org.apache.hadoop.hbase.util.Bytes
//
//import scala.collection.JavaConversions._
//import scala.collection.mutable.{ArrayBuffer, Map}
//
//object HBaseHelper {
//    val conn = getHbaseConnection()
//    val admin = conn.getAdmin
//
//    def getHbaseConfig(conf_kv: Map[String, String]): Configuration = {
//        val HConf = HBaseConfiguration.create()
//
//        conf_kv.keys.foreach { cmd =>
//            HConf.set(cmd, conf_kv(cmd))
//        }
//
//        HConf
//    }
//
//    private def setHColumnDescriptor(hc: HColumnDescriptor, kv: Map[String, String]) {
//        kv.keys.foreach { key =>
//            val value = kv(key)
//            key match {
//                case "versions" => hc.setMaxVersions(value.toInt)
//                case "ttl" => hc.setTimeToLive(value.toInt)
//                case "inMem" => hc.setInMemory(value.toBoolean)
//            }
//        }
//    }
//
//    def getHbaseConnection(): Connection = {
//        val conf_kv = FileHelper.readFile_kv("./DynConfig/hbase.conf", "=")
//        ConnectionFactory.createConnection(getHbaseConfig(conf_kv))
//    }
//
//    def createTable(tableName: String, familys: ArrayBuffer[String], kv: Map[String, String]): Boolean = {
//        try {
//            val table = TableName.valueOf(tableName)
//            if (admin.tableExists(table)) {
//                LogHelper.warn(s"the table: ${tableName} was exist\n")
//                true
//            }
//
//            val tableDescr = new HTableDescriptor(table)
//
//            familys.foreach { f =>
//                val hc = new HColumnDescriptor(f.getBytes)
//                setHColumnDescriptor(hc, kv)
//                tableDescr.addFamily(hc)
//            }
//
//            admin.createTable(tableDescr)
//
//            true
//        } catch {
//            case ex: Exception =>
//                LogHelper.err(ex.getMessage() + "\n")
//                false
//        }
//    }
//
//    def createTableForce(tableName: String, familys: ArrayBuffer[String], kv: Map[String, String]): Boolean =  {
//        try {
//            val table = TableName.valueOf(tableName)
//            if (admin.tableExists(table)) {
//                admin.disableTable(table)
//                admin.deleteTable(table)
//            }
//
//            createTable(tableName, familys, kv)
//        } catch {
//            case ex: Exception =>
//                LogHelper.err(ex.getMessage() + "\n")
//                false
//        }
//    }
//
//    /* when family's name eq Column's name, set column null */
//    def addColumn(tableNameStr: String, key: String, family: String, columnName: String, columnValue: String): Boolean = {
//        try {
//            val tableName = TableName.valueOf(tableNameStr)
//            if (!admin.tableExists(tableName)) {
//                LogHelper.err(s"the table: ${tableNameStr} is not exist\n")
//                false
//            }else {
//                val table = conn.getTable(tableName)
//                val line = new Put(key.getBytes)
//
//                if (columnName == null) line.addColumn(family.getBytes, null, columnValue.getBytes)
//                else line.addColumn(family.getBytes, columnName.getBytes, columnValue.getBytes)
//
//                table.put(line)
//                true
//            }
//        } catch {
//            case ex: Exception =>
//                LogHelper.err(ex.getMessage() + "\n")
//                false
//        }
//    }
//
//    /*
//     *
//     *  data: Map[String, HBaseColumn] Map[key, HBaseColumn]
//     *
//     */
//
//    def addColumnBatch(tableNameStr: String, family: String, data: Map[String, HBaseColumn]): Boolean = {
//        try {
//            val tableName = TableName.valueOf(tableNameStr)
//            if (!admin.tableExists(tableName)) {
//                LogHelper.err(s"the table: ${tableNameStr} is not exist\n")
//                false
//            }else {
//                val table = conn.getTable(tableName)
//                data.keys.foreach { key =>
//                    val column_kv = data(key)
//                    val line = new Put(key.getBytes)
//                    line.addColumn(family.getBytes, column_kv.column.getBytes, column_kv.value.getBytes)
//                    table.put(line)
//                }
//
//                true
//            }
//        } catch {
//            case ex: Exception =>
//                LogHelper.err(ex.getMessage() + "\n")
//                false
//        }
//
//        false
//    }
//
//    def addColumnBatch(tableNameStr: String, key: String, family: String, data: ArrayBuffer[HBaseColumn]): Boolean = {
//        try {
//            val tableName = TableName.valueOf(tableNameStr)
//            if (!admin.tableExists(tableName)) {
//                LogHelper.err(s"the table: ${tableNameStr} is not exist\n")
//                false
//            }else {
//                val table = conn.getTable(tableName)
//                data.foreach { kv =>
//                    val line = new Put(key.getBytes)
//                    line.addColumn(family.getBytes, kv.column.getBytes, kv.value.getBytes)
//                    table.put(line)
//                }
//
//                true
//            }
//        } catch {
//            case ex: Exception =>
//                LogHelper.err(ex.getMessage() + "\n")
//                false
//        }
//
//        false
//    }
//
//    /*
//     *
//     *  data: Map[String, String] Map[key, value]
//     *  the family's Column is null
//     *
//     */
//
//    def addColumnBatchAllFamily(tableNameStr: String, family: String, data: Map[String, String]): Boolean = {
//        try {
//            val tableName = TableName.valueOf(tableNameStr)
//            if (!admin.tableExists(tableName)) {
//                LogHelper.err(s"the table: ${tableNameStr} is not exist\n")
//                false
//            }else {
//                val table = conn.getTable(tableName)
//                data.keys.foreach { key =>
//                    val value = data(key)
//                    val line = new Put(key.getBytes)
//                    line.addColumn(family.getBytes, null, value.getBytes)
//                    table.put(line)
//                }
//
//                true
//            }
//        } catch {
//            case ex: Exception =>
//                LogHelper.err(ex.getMessage() + "\n")
//                false
//        }
//
//        false
//    }
//
//
//    /* when family's name eq Column's name, set column null */
//    def getColumn(tableNameStr: String, key: String, family: String, column: String): String = {
//        try {
//            val tableName = TableName.valueOf(tableNameStr)
//            if (!admin.tableExists(tableName)) {
//                LogHelper.err(s"the table: ${tableNameStr} is not exist\n")
//                Unit.toString
//            }else {
//                val table = conn.getTable(tableName)
//                val g = new Get(key.getBytes)
//                val result = table.get(g)
//                var value = Unit.toString
//                if (column == null) value = Bytes.toString(result.getValue(family.getBytes, null))
//                else value = Bytes.toString(result.getValue(family.getBytes, column.getBytes))
//                if (value == null)  Unit.toString
//                value
//            }
//        } catch {
//            case ex: Exception =>
//                LogHelper.err(ex.getMessage() + "\n")
//                Unit.toString
//        }
//
//        Unit.toString
//    }
//
//    /* when family's name eq Column's name, column set null */
//    def getColumnAtAllVersion(tableNameStr: String, key: String, family: String, column: String): ArrayBuffer[String] = {
//        val values = ArrayBuffer[String]()
//        try {
//            val tableName = TableName.valueOf(tableNameStr)
//            if (!admin.tableExists(tableName)) {
//                LogHelper.err(s"the table: ${tableNameStr} is not exist\n")
//            }else {
//                val table = conn.getTable(tableName)
//                val get = new Get(key.getBytes)
//                get.setMaxVersions()
//                if (column == null ) get.addColumn(family.getBytes, null)
//                else get.addColumn(family.getBytes, column.getBytes)
//                val result = table.get(get).listCells()
//                for (res <- result) {
//                    values += Bytes.toString(CellUtil.cloneValue(res))
//                }
//            }
//        } catch {
//            case ex: Exception =>
//                LogHelper.err("getColumnAtAllVersion: " + ex.getMessage() + "\n")
//        }
//
//        values
//    }
//
//    def getRow(tableNameStr: String, rowKey: String, family: String, column: String): Map[String, String] = {
//
//        val row = Map[String, String]()
//
//        try {
//            val tableName = TableName.valueOf(tableNameStr)
//            if (!admin.tableExists(tableName)) {
//                LogHelper.err(s"HBaseHelper: getRow: ${tableName} not exist")
//            } else {
//                val table = conn.getTable(tableName)
//                val get = new Get(rowKey.getBytes)
//            }
//
//
//        } catch {
//            case ex: Exception =>
//                LogHelper.err(s"HBaseHelper: getRow: ${ex.getMessage()}\n")
//        }
//
//        row
//    }
//
//    def scannerClose(scanner: ResultScanner) {
//        if (scanner != null) scanner.close()
//    }
//
//    def scanTable(tableNameStr: String, family: String): ResultScanner = {
//        val tableName = TableName.valueOf(tableNameStr)
//        val table = conn.getTable(tableName)
//        val s = new Scan()
//        s.addColumn(family.getBytes, null)
//        val scanner = table.getScanner(s)
//        // try{
//        //   for(r <- scanner){
//        //       val hh = Bytes.toString(r.getValue(family.getBytes, null))
//        //   }
//        // }
//        scanner
//    }
//
//    def scanTable(tableNameStr: String, family: String, column: String): ResultScanner = {
//        val tableName = TableName.valueOf(tableNameStr)
//        val table = conn.getTable(tableName)
//        val s = new Scan()
//        s.addColumn(family.getBytes, column.getBytes)
//        val scanner = table.getScanner(s)
//        scanner
//    }
//}
