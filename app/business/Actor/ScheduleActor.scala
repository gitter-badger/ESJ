package business.Actor

import akka.actor.{Actor, ActorRef, ActorSystem, Props}
import akka.event.Logging
import common.CaseClass._
import common.FileHelper.FileHelper
import config.DynConfiguration

import scala.collection.mutable.ArrayBuffer
import scala.concurrent.duration._

class ScheduleActor extends Actor {

  val log = Logging(context.system, this)
  val actors = ArrayBuffer[ActorRef]()
  val dynConf = DynConfiguration.getConf()
  val serviceName = dynConf.getString("Actor.ESJ")
  val queue = dynConf.getString("FQueue.Queue.ESJ")

  private def syncTable(system: ActorSystem): Unit = {
    val syncTableActor = context.actorOf(Props[SyncTableActor], "SyncTableActor")
    import system.dispatcher
    if (FileHelper.fileIsExist(dynConf.getString("Table.ScenePriority"))) {
      context.system.scheduler.schedule(12 hours, 24 hours, syncTableActor, Work("SyncTable"))
    }
  }

  def receive = {
    case Start(system) =>
      println("ScheduleActor: Start(system)")
      var actorNumber = 1
      val num = dynConf.getString("Actor.ESJ.Number")
      if (num != Unit.toString) actorNumber = num.toInt

      var sleepDelay = 1000
      val delay = dynConf.getString("Actor.ESJ.Delay")
      if (delay != Unit.toString) sleepDelay = delay.toInt

      for (i <- 1 to actorNumber) {
        log.info(s"ScheduleActor: the ${i.toString}th ${serviceName} Actor starting...")
        var esjActor = context.actorOf(Props[ESJActor], "ESJActor-" + i.toString)

        actors += esjActor
        esjActor ! Work(queue)
        Thread.sleep(sleepDelay)
      }

        /** update data tables **/

    case Stop =>
      log.info(s"Scheduler: ${serviceName} service stopping...")
      context.stop(self)
    case _ => log.info("Scheduler: invalid message")
  }
}