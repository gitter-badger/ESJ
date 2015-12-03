package services.business

import common.ConfHelper.ConfigHelper
import common.HttpHelper.HttpHelper


/**
 * Created by cwx on 15-11-26.
 */
object ServingLayer {
  val dynConfig = ConfigHelper.getConf()

  def getItemsByUid(uid: String, num: String): String = {
    val service = dynConfig.getString("Oryx.Item")
    try{
      val url = service + "/recommend/" + uid +"?howMany=" + num;
      val dataFromeServing = HttpHelper.getWebPage(url)
      val fields = dataFromeServing.split("\n")
      var Strs = ""
      for (field <- fields) {
        val kv = field.split(",", 2);
        if (kv.size == 2) {
          val Str = kv(0).trim + " "
          Strs += Str
        }
      }

      Strs
    }catch {
      case ex: Exception => {
        Unit.toString()
    }
  }
  }

  def getTagsByUid(uid: String, num: String): String = {
    val service = dynConfig.getString("Oryx.Tag")
    try{
      val url = service + "/recommend/" + uid +"?howMany=" + num;
      val dataFromeServing = HttpHelper.getWebPage(url)
      val fields = dataFromeServing.split("\n")
      var Strs = ""
      for (field <- fields) {
        val kv = field.split(",", 2);
        if (kv.size == 2) {
          val Str = kv(0).trim + " "
          Strs += Str
        }
      }
      Strs
    }catch {
      case ex: Exception => {
        Unit.toString()
      }
    }
  }
}
