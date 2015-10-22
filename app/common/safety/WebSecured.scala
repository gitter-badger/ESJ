/*
 * Copyright (c) 2015, BoDao, Inc. All Rights Reserved.
 *
 * Author@ dgl
 *
 * This software is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
 * CONDITIONS OF ANY KIND, either express or implied.
 *
 * This login security implementations based Security Mechanism play2
 *
 * Providing universal asynchronous two versions.
 * For page requests use the generic version,
 * for rest it is recommended to use the asynchronous version of the requested data,
 * so data acquisition operation into another
 * Thread pool to execute, do not affect the main thread pool play affect the user.
 *
 */


package common.safety


import java.net.InetAddress

import _root_.config.SysConfiguration
import common.CaseClass.WebUser
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.mvc._

import scala.concurrent.Future


trait WebSecured {
    val intAddr = InetAddress.getLocalHost();
    val conf = SysConfiguration.getConf();

    def username(request: RequestHeader) = request.session.get(Security.username);

    def onUnauthorized(request: RequestHeader) = Results.Redirect(s"http://${request.host}/", 302);

    def onUnauthorized2(request: RequestHeader):Future[Result] = {
        Future(Results.Redirect(s"http://${request.host}/", 302));
    }

    def withAuth(f: => String => Request[AnyContent] => Result) = {
        Security.Authenticated(username, onUnauthorized) { username =>
            Action(request => f(username)(request))
        }
    }

    def withAuthAsync(f: => String => Request[AnyContent] => Result) = {
        Security.Authenticated(username, onUnauthorized) { username =>
            Action.async(request => Future(f(username)(request)))
        }
    }

    def withAuthAsync2(f: => String => Request[AnyContent] => Future[Result]) = {
        Security.Authenticated(username, onUnauthorized) { username =>
            Action.async(request => f(username)(request))
        }
    }

    def withUser(f: WebUser => Request[AnyContent] => Result) = withAuth { username =>
        implicit request =>
            WebUserHelper.getUserByUserName(username).map { user =>
                f(user)(request)
            }.getOrElse(onUnauthorized(request))
    }

    def withUserAsync(f: WebUser => Request[AnyContent] => Result) = withAuthAsync { username =>
        implicit request =>
            WebUserHelper.getUserByUserName(username).map { user =>
                f(user)(request)
            }.getOrElse(onUnauthorized(request))
    }

    def withUserAsync2(f: WebUser => Request[AnyContent] => Future[Result]) = withAuthAsync2 { username =>
        implicit request =>
            WebUserHelper.getUserByUserName(username).map { user =>
                f(user)(request)
        }.getOrElse(onUnauthorized2(request))
    }
}
