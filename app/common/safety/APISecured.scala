/*
 * Copyright (c) 2015, BoDao, Inc. All Rights Reserved.
 *
 * Author@ dgl
 *
 * This software is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
 * CONDITIONS OF ANY KIND, either express or implied.
 *
 * safety module, a Api request with test and verify here
 *
 */


package common.safety


import common.CaseClass.ApiUser
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.mvc._

import scala.concurrent.Future


trait APISecured {
    /* the user's name in http header's "u" Field */
    def apiUser(request: RequestHeader): Option[ApiUser] = {
        request.headers.get("u") match {
            case None => None;
            case Some(publicKey) => ApiUsers.getUser(publicKey);
        }
    }

    def onUnauthorized(request: RequestHeader) = Results.NonAuthoritativeInformation

    def withAuth(f: => ApiUser => Request[AnyContent] => Result) = {
        Security.Authenticated(apiUser, onUnauthorized) { apiUser =>
            Action(request => f(apiUser)(request))
        }
    }

    def withAuthAsync(f: => ApiUser => Request[AnyContent] => Result) = {
        Security.Authenticated(apiUser, onUnauthorized) { apiUser =>
            Action.async(request => Future(f(apiUser)(request)))
        }
    }
}
