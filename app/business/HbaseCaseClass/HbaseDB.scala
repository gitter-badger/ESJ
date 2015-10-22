package business.HbaseCaseClass


import scala.collection.mutable.ArrayBuffer
import scala.collection.mutable.Map

import config.DynConfiguration
import common.HbaseHelper.HbaseHelper

object HbaseDb {
    val dynConf = DynConfiguration.getConf();
    val versions = dynConf.getString("hbase.versions");
    val ttl = dynConf.getString("hbase.ttl");
    val businessName = dynConf.getString("business.name");
    val webRawData = businessName + "_WebTrackData";
    val userStatistics = businessName + "_UserStatistics";
    val userInterests = businessName + "_UserInterests";
    val emailTrackingData = businessName + "_EmailTrackingData";
    val emailTaskAnalysis = businessName + "_EmailTaskAnalysis";
    val emailCommand = businessName + "_EmailCommand";

    private def createTable(tableName: String, family: String, force: Boolean): Boolean = {
        val familys = new ArrayBuffer[String]();
        val familysAttr = Map[String, String]();

        familys += family;

		familysAttr += ("versions" -> versions);
		familysAttr += ("ttl" -> ttl);

        if (force) {
            return HbaseHelper.createTableForce(tableName, familys, familysAttr);
        }else {
            return HbaseHelper.createTable(tableName, familys, familysAttr);
        }
    }

    def createWebRawData(force: Boolean): Boolean = {
        val familys = "Tracks";
        return createTable(webRawData, familys, force);
    }

    def createUserStatistics(force: Boolean): Boolean =  {
        val familys = "Statistics";
        return createTable(userStatistics, familys, force);
    }

    def crtetaeUserInterests(force: Boolean): Boolean = {
        val familys = "Interests";
        return createTable(userInterests, familys, force);
    }

    def createEmailTrackingData(force: Boolean): Boolean = {
        val familys = "EmailFeedback";
        return createTable(emailTrackingData, familys, force);
    }

    def createEmailTaskAnalysis(force: Boolean): Boolean =  {
        val familys = "EmailData";
        return createTable(emailTaskAnalysis, familys, force);
    }

    def createEmailCommand(force: Boolean): Boolean = {
        val familys = "Email";
        return createTable(emailCommand, familys, force);
    }

    def createHbaseDb(force: Boolean): Boolean = {
        if (!createWebRawData(force) ) return false;
        if (!createUserStatistics(force)) return false;
        if (!crtetaeUserInterests(force)) return false;
        if (!createEmailTrackingData(force)) return false;
        if (!createEmailTaskAnalysis(force)) return false;
        if (!createEmailCommand(force)) return false;

        return true;
    }
}
