package common.FqueueHelper

/**
 * Created by horatio on 10/27/15.
 */

import common.ConfHelper.ConfigHelper
import net.rubyeye.xmemcached.{MemcachedClient, XMemcachedClientBuilder}
import net.rubyeye.xmemcached.utils.AddrUtil

import scala.util.Try


object FqueueHelper {
  var fqCli: Option[FqueueTools] = None
  val dynConf = ConfigHelper.getConf()
  val addr = dynConf.getString("FQueue.Address")

  def client(): FqueueTools = {
    fqCli match {
      case None =>
        fqCli = Some(new FqueueTools(addr))
        fqCli.get
      case Some(queue) => queue
    }
  }
}

class FqueueTools(addr: String) {
  val builder = new XMemcachedClientBuilder(AddrUtil.getAddresses(addr))
  builder.setConnectionPoolSize(4)
  builder.setConnectTimeout(6000)
  val client: MemcachedClient = builder.build()

  def pull(queue: String): Option[String] = {
    val s = client.get[String](queue)
    if (s == null || s.equals("null"))  None
    else Some(s)
  }

  def push(queue: String, data: String): Boolean = client.set(queue, 0, data)

  def close() {
      Try(this.client.shutdown())
//      match {
//        case Success(ok) =>
//        case Failure(ex: Throwable) =>
//    }
  }
}
