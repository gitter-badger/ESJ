package common.MQHelper

import common.LogHelper.LogHelper
import org.apache.activemq.apollo.stomp.StompFrame
import org.fusesource.stomp.scomp.{StompSubscription, StompClient}

/**
 * Created by cwx on 15-11-28.
 */
object MQHelper {
  var fqueue: Option[MQTools] = None

  def getMqueue(): MQTools = {
    fqueue match {
      case None =>
        fqueue = Some(new MQTools("localhost"))
        fqueue.get
      case Some(queue) => queue
    }
  }
}

class MQTools(qhost: String) {
  val client = new StompClient
  client.connect(qhost, 61613, "admin", "password")


  def sendQueue(qName: String, data: String,correlationId: Option[String] = None, persistent: Boolean = false): Unit = {
    try {
      client.send(qName, data, correlationId, persistent)

    } catch {
      case ex: Exception =>
        LogHelper.err(s"FqueueTools err at sendQueue, ${ex.getMessage}\n")
        return false
    }
  }

  def receiveQueue(destination: String, listener: Option[(StompFrame) => Unit] = None): StompSubscription = {
    try {
      client.subscribe(destination, listener)

    } catch {
      case ex: Exception =>
        LogHelper.err(s"FqueueTools err at receviveQueue, ${ex.getMessage}\n")
        return null
    }
  }


  def close(): Boolean = {
    try{
      this.client.disconnect()
      return true
    } catch {
      case ex: Exception =>
         LogHelper.err(s"FqueueTools err at close, ${ex.getMessage}\n")
        return false
    }
  }
}
