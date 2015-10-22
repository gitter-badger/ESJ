package business.HbaseCaseClass


import common.HbaseHelper.HbaseHelper
import scala.collection.mutable.ArrayBuffer
import common.HbaseHelper.HbaseColumn


class UserInterests(uid: String, tagWeg: String) {
    val tableName = HbaseDb.userInterests;
    val family = "Interests";
    val column = "TagWeg";  /* only one column in UserInterests */

//def addColumn(tableNameStr: String, key: String, family: String, columnName: String, columnValue: String): Boolean = {
    def put2Hbase(): Boolean = {
        if (tagWeg == null && tagWeg == Unit.toString) return false;
        return HbaseHelper.addColumn(tableName, uid, family, column, tagWeg);
    }

    def getRecord(): Boolean = {

        return false;
    }

}
