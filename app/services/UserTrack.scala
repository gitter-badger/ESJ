package services


import business.{CodeMap, CodeMapApp}
import common.AnsjHelper.AnsjHelper
import common.DateHelper.DateHelper
import common.LogHelper.LogHelper
import org.ansj.domain.Term
import rapture.json.formatters.compact
import rapture.json.jsonBackends.lift._
import rapture.json.{Json, _}

import scala.collection.immutable.{Map => IMap}
import scala.collection.mutable.{ArrayBuffer, Map}

class UserTrack(uid: String) {
    val itemsScore = Map[String, Int]();
    val tagsScore = Map[String, Int]();
    val tracks = Map[String, Map[String, ArrayBuffer[String]]]();
    var actions = Map[String, Int]();
    var viewt = "";
    var userTrackRef = "";
    var dur = 0;

    private def megerTracks(ut: UserTrack): Unit = {

    }

    def megerUserTrack(ut: UserTrack): UserTrack = {
        ut.itemsScore.keys.foreach { item =>
            if (itemsScore.contains(item)) {
                itemsScore(item) = itemsScore(item) + ut.itemsScore(item);
            }else {
                itemsScore += (item -> ut.itemsScore(item));
            }
        }

        ut.tagsScore.keys.foreach { item =>
            if (tagsScore.contains(item)) {
                tagsScore(item) = tagsScore(item) + ut.tagsScore(item);
            }else {
                tagsScore += (item -> ut.tagsScore(item));
            }
        }

        ut.actions.keys.foreach { act =>
            if (actions.contains(act)) {
                actions(act) += actions(act) + ut.actions(act);
            }else {
                actions += (act -> ut.actions(act));
            }
        }

        dur += ut.dur;
        if (viewt.toLong > ut.viewt.toLong) viewt = ut.viewt;

        return this;
    }

    private def getTrackTags(codemap: CodeMap, key: String): Array[String] = {
        val tags = codemap.getCodeSafety(key);
        if (tags != Unit.toString) {
            val fields = tags.split(",");
            return fields;
        }else {
            return null;
        }
    }

    private def getUserTrackRef(refs: Map[String, String]): String = {
        return "";
    }

    private def megerTrackTags(tagscode: ArrayBuffer[String], tags: Array[String]): Unit = {
        for (t <- tags) {
            if (!tagscode.contains(t)) tagscode += t;
            IncTagsScore(t);
        }
    }

    private def getTagsByTitle(title: String): Array[String] = {
        val tags = ArrayBuffer[String]();
        val words = AnsjHelper.strParse(title).toArray();
        val codemap = CodeMapApp.getCodeMap("tags");

        if (codemap != null) {
            for (word <- words) {
                val wordName = word.asInstanceOf[Term].getName();
                val tagcode = codemap.getCodeSafety(wordName);
                if (tagcode != Unit.toString) {
                    if (!tags.contains(tagcode)) tags += tagcode;
                }
            }
        }

        return tags.toArray;
    }

    private def addTrackItemMap(trackMap: Map[String, ArrayBuffer[String]], key: String, item: String): Unit =  {
        val trackItems = ArrayBuffer[String]();
        trackItems += (item);
        trackMap += (key -> trackItems);
    }

    private def tagsCode2String(tagscode: ArrayBuffer[String]): String = {
        var ret = "";

        for (tag <- tagscode) {
            ret += (tag + ",");
        }

        if (ret != "" && ret.length > 0) ret = ret.substring(0, ret.length - 1);

        return ret;
    }

    private def toResolveUserTracks(tracksJs: Json): Boolean = {
        if (!getViewTime(tracksJs)) return false;
        val refs = Map[String, String]();
        val userTracksJs = tracksJs.trackers.asInstanceOf[Json].as[Array[Json]];

        for (trackJs <- userTracksJs) {
            val ref = getTrackRef(trackJs);
            val id = getTrackId(trackJs);
            val vtype = getTrackVtype(trackJs);
            val codemap = CodeMapApp.getCodeMap(vtype);
            val created = getTrackCreated(trackJs);

            if (created != Unit.toString && ref != Unit.toString) {
                refs += (created -> ref);
            }

            /* vtype, id, created must in a track */
            if (vtype != Unit.toString && id != Unit.toString && created != Unit.toString && codemap != null) {
                /* record per track infos for this user */
                val trackMap = Map[String, ArrayBuffer[String]]();
                val tagscode = ArrayBuffer[String]();

                // val price = getTrackPrice(trackJs);
                // if (price != Unit.toString) addTrackItemMap(trackMap, "price", price);
                //
                // val sales = getSales(trackJs);
                // if (sales != Unit.toString) addTrackItemMap(trackMap, "sales", sales);

                val pagetime = getTrackPageTime(trackJs);
                if (pagetime != Unit.toString) {
                    addTrackItemMap(trackMap, "dur", pagetime);
                    dur += pagetime.toInt;  /* tatal duration */
                }

                val taction = getTrackAction(trackJs);
                if (taction != Unit.toString) {
                    IncAtcion(taction);
                }

                val title = getTrackTitle(trackJs);
                if (title != Unit.toString) megerTrackTags(tagscode, getTagsByTitle(title));

                /* meger the tags to in this track */
                /* make the Score for the tags in this track */
                val trackTags = getTrackTags(codemap, id);
                if (trackTags != null) megerTrackTags(tagscode, trackTags);

                /* inc a item in this tracks */
                if (vtype == "item") {
                    //get the item's price and salse
                    IncItemsScore(id);
                }

                addTrackItemMap(trackMap, "tags", tagsCode2String(tagscode));

                tracks += ((id + "_" + created) -> trackMap);
            }
        }

        userTrackRef = getUserTrackRef(refs);

        return true;
    }

    def resolveUserTracks(tracksJs: Json): Boolean = {
        try {
            return toResolveUserTracks(tracksJs);
        } catch {
            case ex: Exception =>
                LogHelper.errLoger(s"resolveUserTracks err, ${ex.getMessage}\n");
                return false;
        }
    }

    private def getViewTime(tracksJs: Json): Boolean = {
        viewt = tracksJs.viewt.toString;
        if (viewt == "undefined" || viewt == Unit.toString) {
            LogHelper.warningLoger(s"resolveUserTracks err, viewtime was ${viewt}");
            return false;
        }else {
            viewt = tracksJs.viewt.as[String];
            return true;
        }
    }

    private def getTrackRef(trackJs: Json): String =  {
        val ref = trackJs.ref.toString;
        if (ref != null && ref != "undefined" && ref != "") return trackJs.ref.as[String];
        else return Unit.toString;
    }

    private def getTrackTitle(trackJs: Json): String = {
        val title = trackJs.title.toString;
        if (title != null && title != "undefined" && title != "") return trackJs.title.as[String];
        else return Unit.toString;
    }

    private def getTrackId(trackJs: Json): String = {
        val id = trackJs.id.toString;
        if (id != null && id != "undefined" && id != "") return trackJs.id.as[String];
        else return Unit.toString;
    }

    // private def getTrackPrice(trackJs: Json): String = {
    //     val price = trackJs.price.toString;
    //     if (price != null && price != "undefined" && price != "") return trackJs.price.as[String];
    //     else return Unit.toString;
    // }
    //
    // private def getSales(trackJs: Json): String = {
    //     val sales = trackJs.sales.toString;
    //     if (sales != null && sales != "undefined" && sales != "") return trackJs.sales.as[String];
    //     else return Unit.toString;
    // }

    private def getTrackVtype(trackJs: Json): String =  {
        val vtype = trackJs.vtype.toString;
        if (vtype != null && vtype != "undefined" && vtype != "") return trackJs.vtype.as[String];
        else return Unit.toString;
    }

    private def getTrackCreated(trackJs: Json): String = {
        val created = trackJs.created.toString;
        if (created != null && created != "undefined" && created != "")
            return trackJs.created.as[Long].toString;
        else return Unit.toString;
    }

    private def getTrackPageTime(trackJs: Json): String = {
        val pagetime = trackJs.pageTime.toString;
        if (pagetime != null && pagetime != "undefined" && pagetime != "")
            return trackJs.pageTime.as[Int].toString;
        else return Unit.toString;
    }

    private def getTrackAction(trackJs: Json): String =  {
        val taction = trackJs.action.toString;
        if (taction != null && taction != "undefined" && taction != "")
            return trackJs.action.as[String];
        else return Unit.toString;
    }

    private def IncItemsScore(itemCode: String): Unit = {
        if (itemsScore.contains(itemCode)) {
            itemsScore(itemCode) = itemsScore(itemCode) + 1;
        }else {
            itemsScore += (itemCode -> 1);
        }
    }

    private def IncTagsScore(tagsCode: String): Unit = {
        if (tagsScore.contains(tagsCode)) {
            tagsScore(tagsCode) = tagsScore(tagsCode) + 1;
        }else {
            tagsScore += (tagsCode -> 1);
        }
    }

    private def IncAtcion(taction: String): Unit = {
        if (actions.contains(taction)) actions(taction) = actions(taction) + 1;
        else actions += (taction -> 1);
    }

    /* get the string that format match oryx's kafka ALS's topic */
    private def itemsScore2String(): String = {
        var ret = "";
        val ts = DateHelper.getCurrentTimeSeconds().toString;

        itemsScore.keys.foreach { itemid =>
            ret += s"""${uid},${itemid},${itemsScore(itemid)},${ts}\n""";
        }

        return ret;
    }

    private def tagsScore2String(): String = {
        var ret = "";
        val ts = DateHelper.getCurrentTimeSeconds().toString;

        tagsScore.keys.foreach { tagid =>
            ret += s"""${uid},${tagid},${tagsScore(tagid)},${ts}\n""";
        }

        return ret;
    }

    /* map to string */
    private def tracks2String(): String = {
        var retJs = json"""{}""";
        tracks.keys.foreach { trackid =>
            var trackJs = json"""{}""";
            val track = tracks(trackid);
            track.keys.foreach { trackkey =>
                val jsStr = s"""{ "${trackkey}": "${track(trackkey)(0).toString}" }""";
                val jsObj = Json.parse(jsStr);
                trackJs = trackJs ++ jsObj;
            }

            retJs = retJs ++ Json.parse(s"""{ "${trackid}":${trackJs} }""");
        }

        return Json.format(retJs);
    }

    private def action2String(): String = {
        var ret = "";

        actions.keys.foreach { act =>
            ret += s"${act}:${actions(act)},";
        }

        /* rm the ',' at tail */
        if (ret != "" && ret.length > 0) ret = ret.substring(0, ret.length - 1);

        return ret;
    }

    def getItemsScore(): String = itemsScore2String();
    def getTagsScore(): String = tagsScore2String();
    def getActions(): String = action2String();
    def getUserTracks(): String = tracks2String();
    def getUserTracksViewTime() = viewt;
    def getUserTrackRef() = userTrackRef;
    def getUserTrackTotalDuration() = dur.toString;
}
