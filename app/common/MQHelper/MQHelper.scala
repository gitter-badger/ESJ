package common.MQHelper

import common.ConfHelper.ConfigHelper
import common.LogHelper.LogHelper
import org.apache.activemq.apollo.stomp.StompFrame
import org.fusesource.stomp.scomp.{StompSubscription, StompClient}

/**
 * Created by cwx on 15-11-28.
 */
object MQHelper {
  var mqueue: Option[MQTools] = None
  val dynConf = ConfigHelper.getConf()
  val addr = dynConf.getString("MQueue.Address")

  def getMqueue(): MQTools = {
    mqueue match {
      case None =>
        mqueue = Some(new MQTools(addr))
        mqueue.get
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
    }
  }

  def receiveQueue(destination: String, listener: Option[(StompFrame) => Unit] = None): StompSubscription = {
    try {
      client.subscribe(destination, listener)

    } catch {
      case ex: Exception =>
        LogHelper.err(s"FqueueTools err at receviveQueue, ${ex.getMessage}\n")
         null
    }
  }


  def close(): Boolean = {
    try{
      this.client.disconnect()
       true
    } catch {
      case ex: Exception =>
         LogHelper.err(s"FqueueTools err at close, ${ex.getMessage}\n")
         false
    }
  }
}
