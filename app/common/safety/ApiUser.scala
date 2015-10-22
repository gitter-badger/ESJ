/*
 * Copyright (c) 2015, BoDao, Inc. All Rights Reserved.
 *
 * Author@ dgl
 *
 * This software is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
 * CONDITIONS OF ANY KIND, either express or implied.
 *
 * only one API user in this Program
 *
 */


package common.safety


import common.CaseClass.ApiUser


object ApiUsers {
    def getUser(publicKey: String): Option[ApiUser] = {
        var result: Option[ApiUser] = None;
        if (publicKey == "bodao@qq.com")
            result = Some(ApiUser("bodao@qq.com", "BOdao2015*"));
        return result;

    }
}
