package common.RedisHelper

import com.redis.RedisClientPool
import common.LogHelper.LogHelper
import config.DynConfiguration

import scala.collection.mutable.ListBuffer
import scala.util.{Try, Success, Failure}

object RedisHelper {
  val dynconf = DynConfiguration.getConf()
  val IP = dynconf.getString("Redis.IP")
  val port = dynconf.getString("Redis.Port").toInt
  val cliPool = new RedisClientPool(IP, port)

  /***** push elements of list into Redis stack from right to left *****/
  def rpush(key: String, msgs: List[String]): Long = pushInParallel(key, "r", msgs)
  /***** push elements of list into Redis stack from left to right *****/
  def lpush(key: String, msgs: List[String]): Long = pushInParallel(key, "l", msgs)
  /***** pop elements of Redis stack into list from top to bottom *****/
  def lpop(key: String, iter: Long): ListBuffer[String] = popInParallel(key, "t", iter)
  /***** pop elements of Redis stack into list from bottom to top *****/
  def rpop(key: String, iter: Long): ListBuffer[String] = popInParallel(key, "b", iter)

  private def pushInParallel(key: String, flag: String, msgs: List[String]): Long = {
    var llen = 0L
    Try(cliPool.withClient {
      cli => {
        llen = cli.llen(key).get
        flag match {
          case "r" => Try(msgs.foreach(cli.rpush(key, _))) match {
            case Failure(e) => println(s"RedisHelper: rpush: $e\n")
            case Success(_) =>
          }

          case "l" => Try(msgs.foreach(cli.rpush(key, _).get)) match {
            case Failure(e) => println(s"RedisHelper: lpush: $e\n")
            case Success(_) =>
          }
        }
        llen = cli.llen(key).get - llen
      }
    }) match {
      case Failure(e) => LogHelper.errLoger(s"RedisHelper: pushInParallel: $e\n")
      case Success(_) =>
    }

    llen
  }

  private def popInParallel(key: String, flag: String, iter: Long): ListBuffer[String] = {
    val msgs = ListBuffer[String]()

    Try(cliPool.withClient {
      cli => {
        flag match {
          case "t" =>
            for (i <- 0L until iter) {
              Try(cli.lpop(key).get) match {
                case Failure(e) => println(s"RedisHelper: lpop: $e\n")
                case Success(v) => msgs += v.toString
              }
            }

          case "b" =>
            for (i <- 0L to iter) {
              Try(cli.rpop(key).get) match {
                case Failure(e) => println(s"RedisHelper: rpop: $e\n")
                case Success(v) => msgs += v.toString
              }
            }
        }
      }
    }) match {
      case Failure(e) => LogHelper.errLoger(s"RedisHelper: pop: $e\n")
      case Success(_) =>
    }

    msgs
  }

  //  private def pushInParallel(key: String, flag: String, msgs: List[String]): Long = {
  //    cliPool.withClient {
  //      cli => {
  //        val llen = cli.llen(key).get
  //        try {
  //          flag match {
  //            case "r" => msgs.foreach(cli.rpush(key, _))
  //            case "l" => msgs.foreach(cli.lpush(key, _))
  //          }
  //        } catch {
  //          case e: Exception => LogHelper.errLoger(s"RedisHelper: rpushInParallel: $e\n")
  //        }
  //        cli.llen(key).get - llen
  //      }
  //    }
  //  }

  //  private def popInParallel(key: String, flag: String): Long = {
  //    var llen = 0L
  //    Try(cliPool.withClient {
  //      cli => {
  //        llen = cli.llen(key).get
  //        flag match {
  //          case "r" => Try(cli.rpop(key)) match {
  //            case Failure(e) =>
  //            case Success(v) =>
  //          }
  //          case "l" => Try(cli.rpush(key, _)) match {
  //            case Failure(e) =>
  //            case Success(v) =>
  //          }
  //        }
  //        llen -= cli.llen(key).get
  //      }
  //    }) match {
  //      case Failure(e) => LogHelper.errLoger(s"RedisHelper: popInParallel: $e\n")
  //      case Success(v) =>
  //    }
  //    llen
  //  }

}