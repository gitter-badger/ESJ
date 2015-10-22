/*
 * Copyright (c) 2015, BoDao, Inc. All Rights Reserved.
 *
 * Author@ dgl
 *
 * This software is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
 * CONDITIONS OF ANY KIND, either express or implied.
 *
 * when a API request Arrivals, Decode the ts at http header's "d" field;
 * 3 feild at http header. are "d", "p", "u"
 * "u": user's name
 * "d": the system time of seconds.
 * "p": sha-1(username + encode(d) + md5(user's password))
 *
 */


package common.safety


import scala.collection.mutable.Map


object Decrypt {
    val mapTs = Map('a' -> "0", 's' -> "1", 'd' -> "2", 'f' -> "3",
         'g' -> "4", 'h' -> "5", 'j' -> "6", 'k' -> "7", 'l' -> "8", 'z' -> "9");

    def tsDecode(tsEn: String): String = {
        if (tsEn.length <= 6) return Unit.toString;
        var tsUse = tsEn.substring(4, tsEn.length);
        tsUse = tsUse.substring(0, tsUse.length - 2);
        val tsCh = tsUse.toCharArray();
        var tsStr = "";
        var ret = "";
        var len = 0;

        tsCh.foreach { ch =>
            if (mapTs.contains(ch)) tsStr += mapTs(ch);
            else return Unit.toString;
        }

        val tsDesc = (tsStr.toLong - 183).toString().toCharArray();
        len = tsDesc.length - 1;
        for (i <- 0 to len) {
            ret += tsDesc(len - i).toString;
        }

        // if (ret.length < 13) {  /* for nodejs, fix time length */
        //     var zero = ""
        //     for (i <- 0 to 13 - ret.length - 1) {
        //         zero += "0";
        //     }
        //
        //     ret += zero;
        // }

        return ret;
    }
}