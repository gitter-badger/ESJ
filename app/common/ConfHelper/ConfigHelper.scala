package common.ConfHelper

import common.FileHelper.FileHelper

import scala.collection.mutable.{ArrayBuffer, Map}
import scala.io.Source

/**
 * Created by horatio on 10/28/15.
 */
object ConfigHelper {

  var conf: Option[DynConfig] = None
  def getConf(): DynConfig = {
    conf match {
      case None =>
        val conf_ = DynConfigFactory.load("./DynConfig/dynamic.conf")
        conf = Some(conf_)
        conf_
      case Some(conf) => conf
    }
  }

  def getConf(path: String, separator: String): Map[String, String] = {
    val conf = Map[String, String]()
    try {
      if (!FileHelper.fileIsExist(path)) println(s"${path} is not found")
      else {
        val source = Source.fromFile(path)
        val lines = source.getLines()
        for (line <- lines) {
          val l = line.trim
          if (l != "" && l.length > 1 && l.charAt(0) != '#') {
            val fields = l.split(separator, 2)
            if (!conf.contains(fields(0).trim)) conf += (fields(0).trim -> fields(1).trim)
          }
        }
        source.close()
      }
    } catch {
      case ex: Exception =>
        println(ex.getMessage())
    }

    conf
  }
}

object DynConfigFactory {
  def load(configFile: String): DynConfig = {
    if (!FileHelper.fileIsExist(configFile))
       new DynConfig(new ArrayBuffer[String]())	/* null */
    else {
      val lines = FileHelper.getFileLinesTrim(configFile)
       new DynConfig(lines)
    }
  }
}


class DynConfig(kvs: ArrayBuffer[String]) {
  val conf = Map[String, String]()
  initConfing(kvs)

  def getString(key: String): String = {
    if (conf.contains(key))  conf(key).toString
    else Unit.toString
  }

  private def rmQuotes(str: String): String = {
    if (str.length > 0) {
      if (str.charAt(0) == '\"' && str.charAt(str.length - 1) == '\"')
         str.substring(1, str.length - 1)
      else str
    }else  str
  }

  private def initConfing(kvs: ArrayBuffer[String]) {
    kvs.foreach { kv =>
      if (kv.trim.length > 0 && kv.trim.charAt(0) != '#') {	/* remove '\n' and notation lines */
      val fields = kv.trim().split("=", 2)
        if (!conf.contains(fields(0).trim())) {
          conf += (fields(0).trim -> rmQuotes(fields(1).trim()))
        }
      }
    }
  }
}
