/*
 * Copyright (c) 2015, BoDao, Inc. All Rights Reserved.
 *
 * Author@ dgl
 *
 * This software is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
 * CONDITIONS OF ANY KIND, either express or implied.
 *
 * get web user's info by user' name or email here.
 *
 */


package common.safety


import scala.collection.mutable._
import common.FileHelper.FileHelper
import common.CaseClass.WebUser
import config.SysConfiguration


object WebUserHelper {
    private def getWebUser(file: String): ArrayBuffer[WebUser] = {
        val webUsers = new ArrayBuffer[WebUser]();
        val fileUsers = FileHelper.getFileLinesTrim(file);

        for (fUser <- fileUsers) {
            val field = fUser.trim.split(",", 2);
            webUsers += WebUser(field(0), field(1));   /* email, password */
        }

        return webUsers;
    }

    def getUserByUserName(userName : String): Option[WebUser] = {
        var result: Option[WebUser] = None;
        val cfg = SysConfiguration.getConf();
        val webUsers = getWebUser(cfg.getString("WebUsers.file"));

        for (user <- webUsers) {
            if (user.email == userName) result = Some(user);
        }

        return result;
    }

    def getUserByEmailAndPassword(email : String, password: String): Option[WebUser] = {
        var result: Option[WebUser] = None;
        val cfg = SysConfiguration.getConf();
        val webUsers = getWebUser(cfg.getString("WebUsers.file"));

        for (user <- webUsers) {
            if (user.email == email && user.password == password) result = Some(user);
        }

        return result;
    }
}
