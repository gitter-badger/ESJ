import business.Actor.ScheduleActor
import business.DataManager.HBase
import common.CaseClass._
import common.LogHelper.LogHelper
import play.api.mvc.RequestHeader
import play.api.{GlobalSettings, _}
import akka.actor.{Props, ActorSystem}

object Global extends GlobalSettings {

  var actorSystem: Option[ActorSystem] = None
  private def initService(): Boolean = {
    val force = false
    if (!HBase.initTable(force)) {
      LogHelper.errLoger("Global: init service failed\n")
      return false
    }
    true
  }

  override def onStart(app: Application) {
    Logger.info("Application starting...\n")
    LogHelper.infoLoger("Global service starting...\n")

    /** wait for other objects to start **/
    Thread.sleep(1000)

    if (initService()) {
      val system = ActorSystem("ESJ")
      actorSystem = Some(system)
      val scheduler = system.actorOf(Props[ScheduleActor], "scheduleActor")
      scheduler ! Start(system)
      super.onStart(app)
    }
  }

  override def onStop(app: Application) {
    Logger.info("Application stopping...\n")

    actorSystem.map(actor => {
      actor.actorSelection("akka://ESJ/user/scheduleActor") ! Stop
      actor.shutdown()
    })
    super.onStop(app)
  }

  override def onError(request: RequestHeader, ex: Throwable) = {
    super.onError(request, ex)
  }
}
