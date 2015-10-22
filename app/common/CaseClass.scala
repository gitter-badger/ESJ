

package common.CaseClass

import akka.actor.ActorSystem


case class WebUser(email: String, password: String);
case class ApiUser(username: String, password: String);
case class Start(system: ActorSystem);
case class Work(queue: String);
case class Stop();
