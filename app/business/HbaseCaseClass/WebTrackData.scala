package business.HbaseCaseClass


import common.HbaseHelper.HbaseHelper
import scala.collection.mutable.ArrayBuffer
import common.HbaseHelper.HbaseColumn

case class Tracks(vt: String, ref: String, act: String,
     dur: String, pageInfos: String);


class WebTrackData(uid: String, tracks: Tracks) {
    val tableName = HbaseDb.webRawData;
    val family = "Tracks";
    val vt_c = "VisitTime"
    val ref_c = "referrer";
    val act_c = "Action";
    val dur_c = "Duration";
    val pis_c = "PageInfos";

    def this(uid: String, vt: String, ref: String, act: String,
         dur: String,  pageInfos: String) = {
        this(uid, Tracks(vt, ref, act, dur, pageInfos));
    }

    def put2Hbase(): Boolean = {
        if (tracks == null) return false;
        val data = ArrayBuffer[HbaseColumn]();

        if (tracks.vt != null && tracks.vt != Unit.toString) data += HbaseColumn(vt_c, tracks.vt);
        if (tracks.ref != null && tracks.ref != Unit.toString) data += HbaseColumn(ref_c, tracks.ref);
        if (tracks.act != null && tracks.act != Unit.toString) data += HbaseColumn(act_c, tracks.act);
        if (tracks.dur != null && tracks.dur != Unit.toString) data += HbaseColumn(dur_c, tracks.dur);
        if (tracks.pageInfos != null && tracks.pageInfos != Unit.toString)
            data += HbaseColumn(pis_c, tracks.pageInfos);

        return HbaseHelper.addColumnBatch(tableName, uid, family, data);
    }

    def getRecord(): Boolean = {

        return false;
    }
}
