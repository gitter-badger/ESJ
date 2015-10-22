package business.HbaseCaseClass

import common.HbaseHelper.HbaseHelper
import scala.collection.mutable.ArrayBuffer
import common.HbaseHelper.HbaseColumn


case class Statistics(vc: String, totalDuration: String);


class UserStatistics(uid: String, statis: Statistics) {
    val tableName = HbaseDb.userStatistics;
    val family = "Statistics";
    val vc_c = "VisitCount";
    val td_c = "TotalDuration";

    def this(uid: String, vc: String, totalDuration: String) = {
        this(uid, Statistics(vc, totalDuration));
    }

    def put2Hbase(): Boolean = {
        if (statis == null) return false;
        val data = ArrayBuffer[HbaseColumn]();

        if (statis.vc != null && statis.vc != Unit.toString)
            data += HbaseColumn(vc_c, statis.vc);
        if (statis.totalDuration != null && statis.totalDuration != Unit.toString)
            data += HbaseColumn(td_c, statis.totalDuration);

        return HbaseHelper.addColumnBatch(tableName, uid, family, data);
    }

    def getRecord(): Boolean = {

        return false;
    }
}
