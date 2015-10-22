/*
 * Copyright (c) 2015, BoDao, Inc. All Rights Reserved.
 *
 * Author@ dgl
 *
 * This software is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
 * CONDITIONS OF ANY KIND, either express or implied.
 *
 * when send a API request, encode the ts an put it to http header's "d" field;
 * 3 feild at http header. are "d", "p", "u"
 * "u": user's name
 * "d": the system time of seconds.
 * "p": sha-1(username + encode(d) + md5(user's password))
 *
 */


package common.safety


import scala.collection.mutable.Map
import scala.util.Random


object Encrypt {
    val mapTs = Map('0' -> "a", '1' -> "s", '2' -> "d", '3' -> "f",
         '4' -> "g", '5' -> "h", '6' -> "j", '7' -> "k", '8' -> "l", '9' -> "z");

     def tsEncode(tsStr: String): String = {
        val tsChr = tsStr.toCharArray();
        var tsDesc = "";
        var tsEn = "";
        val len = tsChr.length - 1;

        /* Descending the ts str */
        for (i <- 0 to len) {
            tsDesc += tsChr(len - i).toString();
        }

        /* map to char */
        tsDesc = (tsDesc.toLong + 183).toString();
        tsDesc.toCharArray().foreach { ch =>
            if (mapTs.contains(ch)) tsEn += mapTs(ch);
            else return Unit.toString;
        }

        /* add 4 chars in the ts start */
        for (i <- 0 to 3) {
            val r = Random.nextInt(26) + 97;
            tsEn = r.toChar.toString + tsEn;
        }

        /* add 2 chars in the ts tail */
        for (i <- 0 to 1) {
            val r = Random.nextInt(26) + 97;
            tsEn = tsEn + r.toChar.toString;
        }

        return tsEn;
    }

    def getEncryptedTs(): String = {
        val ts = System.currentTimeMillis / 1000;
        val tsEn = tsEncode(ts.toString());
        return tsEn;
    }

    def getEncryptData(user: String, tsEn: String, passwd: String): String = {
        /* md5 the ts and password */
        val tsMD5 = MD5.string2MD5(tsEn);
        val pwMD5 = MD5.string2MD5(passwd);
        val data = s"${user}${tsMD5}${pwMD5}";
        /* sha1 the data to be the "p" at http's header */
        val dataEn = AuthTools.encode("SHA-1", data).get;
        return dataEn;
    }
}
