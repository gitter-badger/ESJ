package business.HbaseCaseClass


import common.HbaseHelper.HbaseHelper
import scala.collection.mutable.ArrayBuffer
import common.HbaseHelper.HbaseColumn


case class EmailData(recievers: String, title: String,
     content: String, delive: String, open: String, click: String);

class EmailTaskAnalysis(uid: String, emailData: EmailData) {
    val tableName = HbaseDb.emailTaskAnalysis;
    val family = "EmailData";
    val rcv_c = "Recievers";
    val title_c = "Title";
    val cont_c = "Content";
    val delive_c = "Delivery";
    val open_c = "Open";
    val click_c = "Click";

    def this(uid: String, recievers: String, title: String,
         content: String, delive: String, open: String, click: String) = {
        this(uid, EmailData(recievers, title, content, delive, open, click));
    }

    def put2Hbase(): Boolean = {
        if (emailData == null) return false;
        val data = ArrayBuffer[HbaseColumn]();

        if (emailData.recievers != null && emailData.recievers != Unit.toString)
            data += HbaseColumn(rcv_c, emailData.recievers);
        if (emailData.title != null && emailData.title != Unit.toString)
            data += HbaseColumn(title_c, emailData.title);
        if (emailData.content != null && emailData.content != Unit.toString)
            data += HbaseColumn(cont_c, emailData.content);
        if (emailData.delive != null && emailData.delive != Unit.toString)
            data += HbaseColumn(delive_c, emailData.delive);
        if (emailData.open != null && emailData.open != Unit.toString)
            data += HbaseColumn(open_c, emailData.open);
        if (emailData.click != null && emailData.click != Unit.toString)
            data += HbaseColumn(click_c, emailData.click);

        return HbaseHelper.addColumnBatch(tableName, uid, family, data);
    }

    def getRecord(): Boolean = {
        return false;
    }
}
