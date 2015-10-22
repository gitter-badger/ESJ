package business.HbaseCaseClass


import common.HbaseHelper.HbaseHelper
import scala.collection.mutable.ArrayBuffer
import common.HbaseHelper.HbaseColumn


case class EmailFeedback(sbuj: String, scene: String, priority: String,
     sendTime: String, tags: String, items: String, status: String, stayTime: String);

class EmailTrackingData(uid: String, email: EmailFeedback) {
    val tableName = HbaseDb.emailTrackingData;
    val family = "EmailFeedback";
    val sbuj_c = "Subject";
    val scene_c = "SceneId";
    val prio_c = "Priority";
    val sendTime_c = "SendTime";
    val tags_c = "Tags";
    val items_c = "Items";
    val status_c = "Status";
    val stayTime_c = "StayTime";

    def this(uid: String, sbuj: String, scene: String, priority: String,
         sendTime: String, tags: String, items: String, status: String, stayTime: String) = {
        this(uid, EmailFeedback(sbuj, scene, priority, sendTime, tags, items, status, stayTime));
    }

    def put2Hbase(): Boolean = {
        if (email == null) return false;

        val data = ArrayBuffer[HbaseColumn]();
        if (email.sbuj != null && email.sbuj != Unit.toString) data += HbaseColumn(sbuj_c, email.sbuj);
        if (email.scene != null && email.scene != Unit.toString) data += HbaseColumn(scene_c, email.scene);
        if (email.priority != null && email.priority != Unit.toString)
            data += HbaseColumn(prio_c, email.priority);
        if (email.sendTime != null && email.sendTime != Unit.toString)
            data += HbaseColumn(sendTime_c, email.sendTime);
        if (email.tags != null && email.tags != Unit.toString) data += HbaseColumn(tags_c, email.tags);
        if (email.items != null && email.items != Unit.toString) data += HbaseColumn(items_c, email.items);
        if (email.status != null && email.status != Unit.toString) data += HbaseColumn(status_c, email.status);
        if (email.stayTime != null && email.stayTime != Unit.toString)
            data += HbaseColumn(stayTime_c, email.stayTime);

        return HbaseHelper.addColumnBatch(tableName, uid, family, data);
    }

    def getRecord(): Boolean = {
        return false;
    }
}
