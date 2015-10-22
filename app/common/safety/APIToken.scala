/*
 * Copyright (c) 2015, BoDao, Inc. All Rights Reserved.
 *
 * Author@ dgl
 *
 * This software is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
 * CONDITIONS OF ANY KIND, either express or implied.
 *
 * safety module, get the request's header's tokens.
 * the token key are "p", "u", "d"
 * "u": user's name
 * "d": the system time of seconds.
 * "p": sha-1(username + encode(d) + md5(user's password))
 *
 */


package common.safety


import scala.collection.mutable.ArrayBuffer
import scala.collection.mutable.Map

import play.api._
import play.api.mvc._


object APIToken {
    def getRequestVerifToken(request: Request[AnyContent]): Map[String, String] = {
        val tokens = Map[String, String]();

        val p = request.headers.get("p");
        p match {
            case None => return null;
            case Some(pw) =>
                tokens  += ("p" -> pw);
        }

        val u = request.headers.get("u");
        u match {
            case None => return null;
            case Some(user) =>
                tokens += ("u" -> user);
        }

        val d = request.headers.get("d");
        d match {
            case None => return null;
            case Some(dts) =>
                tokens += ("d" -> dts);
        }

        return tokens;
    }
}
