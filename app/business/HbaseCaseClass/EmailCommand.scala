package business.HbaseCaseClass

import common.HbaseHelper.HbaseHelper
import scala.collection.mutable.ArrayBuffer
import common.HbaseHelper.HbaseColumn


case class Email(scene: String, priority: String, sendTime: String, tags: String, items: String);

class EmailCommand(uid: String, email: Email) {
    val tableName = HbaseDb.emailCommand;
    val family = "Email";
    val scene_c = "SceneId";
    val prio_c = "Priority";
    val st_c = "SendTime";
    val tags_c = "Tags";
    val items_c = "Items";

    def this(uid: String, scene: String,
         priority: String, sendTime: String, tags: String, items: String) = {
        this(uid, Email(scene, priority, sendTime, tags, items));
    }

    def put2Hbase(): Boolean = {
        if (email == null) return false;
        val data = ArrayBuffer[HbaseColumn]();
        
        if (email.scene != null && email.scene != Unit.toString) data += HbaseColumn(scene_c, email.scene);
        if (email.priority != null && email.priority != Unit.toString)
            data += HbaseColumn(prio_c, email.priority);
        if (email.sendTime != null && email.sendTime != Unit.toString)
            data += HbaseColumn(st_c, email.sendTime);
        if (email.tags != null && email.tags != Unit.toString)
            data += HbaseColumn(tags_c, email.tags);
        if (email.items != null && email.items != Unit.toString)
            data += HbaseColumn(items_c, email.items);

        return HbaseHelper.addColumnBatch(tableName, uid, family, data);
    }

    def getRecord(): Boolean = {
        return false;
    }
}
