/*
 * Copyright (c) 2015, BoDao, Inc. All Rights Reserved.
 *
 * Author@ dgl
 *
 * This software is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
 * CONDITIONS OF ANY KIND, either express or implied.
 *
 * str's SHA-1 encode here.
 *
 */


package common.safety


import java.security.MessageDigest

import scala.collection.mutable


object AuthTools {
    val users = new mutable.HashMap[String, String]();

    /* sha-1 encode */
    def encode(algorithm: String, content: String): Option[String] = {
        val HEX_DIGITS = Array( '0', '1', '2', '3', '4',
             '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' );

        def getFormattedText(bytes: Array[Byte]): String = {
            val len = bytes.length;
            val buf = new StringBuilder(len*2);
            for (b <- bytes) {
                buf.append(HEX_DIGITS((b >> 4) & 0x0f));
                buf.append(HEX_DIGITS(b & 0x0f));
            }
            buf.toString;
        }

        content match {
            case null => None;
            case content =>
                val messageDigest  = MessageDigest.getInstance(algorithm);
                messageDigest.update(content.getBytes());
                Some(getFormattedText(messageDigest.digest()));
        }
    }

    /* used by web */
    def auth(username: String, ts: Long, password: String, hash_code: String): Boolean = {
        val content = s"${username}${ts}${password}";
        encode("SHA-1", content) match {
            case None => false;
            case Some(sha1) => hash_code == sha1;
        }
    }

    /* used by api */
    def auth(username: String, ts: String, password: String, hash_code: String): Boolean = {
        // val sysTs: Int = (System.currentTimeMillis / 1000).toInt;
        // val tsDe = Decrypt.tsDecode(ts).toInt;
        // var dt: Int = 0;
        //
        // if (sysTs > tsDe) dt = sysTs - tsDe;
        // else dt = tsDe - sysTs;
        //
        // if (dt >= 60) {
        //     LogHelper.errLoger(s"auth err, ts too small, dt == ${dt}, systs == ${sysTs} tsDe == ${tsDe}");
        //     return false;
        // }


        val pwMD5 = MD5.string2MD5(password);
        val tsMD5 = MD5.string2MD5(ts);
        val content = s"${username}${tsMD5}${pwMD5}";
        encode("SHA-1", content) match {
            case None => false;
            case Some(sha1) => hash_code == sha1;
        }
    }
}
