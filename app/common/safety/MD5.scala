/*
 * Copyright (c) 2015, BoDao, Inc. All Rights Reserved.
 *
 * Author@ dgl
 *
 * This software is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
 * CONDITIONS OF ANY KIND, either express or implied.
 *
 * a md5 class here.
 * make the string to md5 string
 *
 */


package common.safety


import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;


object MD5 {
    def string2MD5(inStr: String): String = {
        var md5: MessageDigest = null;
        try{
            md5 = MessageDigest.getInstance("MD5");
        } catch {
			case ex: Exception => {
                println(ex.getMessage);
                return "false";
            }
		}

        var charArray = inStr.toCharArray();
        var byteArray = new Array[Byte](charArray.length);

        for (i <- 0 to charArray.length - 1) {
            byteArray(i) = charArray(i).toByte;
        }

        var md5Bytes = md5.digest(byteArray);
        var hexValue = new StringBuffer();

        for (i <- 0 to md5Bytes.length - 1) {
            var tmp = (md5Bytes(i).toInt) & 0xff;
            if (tmp < 16) hexValue.append("0");
            hexValue.append(Integer.toHexString(tmp));
        }

        return hexValue.toString();
    }
}
