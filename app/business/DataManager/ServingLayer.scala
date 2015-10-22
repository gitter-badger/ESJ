package business.DataManager

import common.HttpHelper.HttpHelper
import common.LogHelper.LogHelper
import config.DynConfiguration
import rapture.json._
import rapture.json.jsonBackends.lift._

/**
  query ALS result on hdfs through Oryx2 serving layer API
 **/

object ServingLayer {
  val dynConf = DynConfiguration.getConf();
  val itemAddress = dynConf.getString("Oryx.Item")
  val tagAddress = dynConf.getString("Oryx.Tag")

  def getItemInterestsByUid(uid: String, num: String): Json = {
    var jsObjs = json"""{}""";

    try {
      val url = itemAddress + "/recommend/" + uid +"?howMany=" + num;
      val dataFromeServing = HttpHelper.getWebPage(url);
      val fields = dataFromeServing.split("\n");
      var count = num.toInt;

      for (field <- fields) {
        val kv = field.split(",", 2);
        if (kv.size == 2) {
          val jsStr = s"""{ "${kv(0).trim}": ${count} }""";
          val js = Json.parse(jsStr);
          jsObjs = jsObjs ++ js;
          count = count - 1;
        }
      }
    } catch {
      case ex: Exception =>
        LogHelper.errLoger(s"Oryx2 getItemInterestsByUid make a err, ${ex.getMessage}\n");
        return json"""{}""";
    }

    return jsObjs;
  }

  def getTagInterestsByUid(uid: String, num: String): Json = {
    var jsObjs = json"""{}""";

    try {
      val url = tagAddress + "/recommend/" + uid +"?howMany=" + num;
      val dataFromeServing = HttpHelper.getWebPage(url);
      val fields = dataFromeServing.split("\n");
      var count = num.toInt;

      for (field <- fields) {
        val kv = field.split(",", 2);
        if (kv.size == 2) {
          val jsStr = s"""{ "${kv(0).trim}": ${count} }""";
          val js = Json.parse(jsStr);
          jsObjs = jsObjs ++ js;
          count =count - 1;
        }
      }
    } catch {
      case ex: Exception =>
        LogHelper.errLoger(s"Oryx2 getTagInterestsByUid make a err, ${ex.getMessage}\n");
        return json"""{}""";
    }

    return jsObjs;
  }


  def getNumOfAllItem(): Int = {

    try {
      val dataFromServing = HttpHelper.getWebPage(itemAddress);
      val fields = dataFromServing .split("\n");
      val num = fields.size;
      return  num;
    } catch {
      case ex: Exception =>
        LogHelper.errLoger(s"Oryx2 getNumOfAllItem make a err, ${ex.getMessage}\n");
        return -1;
    }
  }

  def getSumOfAllItem(): Double = {

    val num = getNumOfAllItem().toString;

    try {
      val url = itemAddress + "/mostPopularItems?howMany=" + num;
      val dataFromServing = HttpHelper.getWebPage(url);
      var sum = 0.0;
      val fields = dataFromServing.split("\n");

      for (field <- fields) {
        val kv = field.split(",");
        sum += kv(1).toDouble;
      }

      return sum;
    }catch {
      case ex: Exception =>
        LogHelper.errLoger(s"Oryx2 getSumOfAllItem make a err, ${ex.getMessage}\n");
        return -1.0;
    }
  }

  def  getItemsDistribution(num: String, ratio: Boolean): Json = {
    var jsObjs = json"""{}""";

    val sum = getSumOfAllItem();
    try {
      var sumOther = 0.0;
      val url = itemAddress + "/mostPopularItems?howMany=" + num;
      val dataFromServing = HttpHelper.getWebPage(url);
      val fields = dataFromServing.split("\n");

      for (field <- fields) {
        val kv = field.split(",");
        var value = 0.0

        if (ratio) {
          value = kv(1).toInt / sum;
          sumOther += value
        } else value = kv(1).toInt

        jsObjs = jsObjs ++ Json.parse(s"""{ "${kv(0).trim}": $value }""")
      }

      if (ratio) jsObjs = jsObjs ++ Json.parse(s"""{ "rest": ${1 - sumOther} }""")

    } catch {
      case ex: Exception =>
        LogHelper.errLoger(s"Oryx2 getItemsDistribution make a err, ${ex.getMessage}\n");
        return json"""{}""";
    }

    return jsObjs;
  }

  def getNumOfAllTags(): Int = {

    try {
      val dataFromServing = HttpHelper.getWebPage(tagAddress);
      val fields = dataFromServing .split("\n");
      val num = fields.size;
      return  num;
    } catch {
      case ex: Exception =>
        LogHelper.errLoger(s"Oryx2 getNumOfAllTags make a err, ${ex.getMessage}\n");
        return -1;
    }
  }

  def getSumOfAllTags(): Double = {

    val num = getNumOfAllItem().toString;

    try {
      val url = tagAddress + "/mostPopularItems?howMany=" + num;
      val dataFromServing = HttpHelper.getWebPage(url);
      var sum = 0.0;
      val fields = dataFromServing.split("\n");

      for (field <- fields) {
        val kv = field.split(",");
        sum += kv(1).toDouble;
      }

      return sum;
    }catch {
      case ex: Exception =>
        LogHelper.errLoger(s"Oryx2 getSumOfAllTags make a err, ${ex.getMessage}\n");
        return -1.0;
    }
  }

  def  getTagsDistribution(num: String, ratio: Boolean): Json = {
    var jsObjs = json"""{}""";

    val sum = getSumOfAllItem();

    try {
      var sumOther = 0.0;
      val url = tagAddress + "/mostPopularItems?howMany=" + num;
      val dataFromServing = HttpHelper.getWebPage(url);
      val fields = dataFromServing.split("\n");

      for (field <- fields) {
        val kv = field.split(",");
        var value = 0.0

        if (ratio) {
          value = kv(1).toInt /  sum;
          sumOther += value;
        } else value = kv(1).toInt

        val jsStr = s"""{ "${kv(0).trim}": $value }""";
        val js = Json.parse(jsStr);
        jsObjs = jsObjs ++ js;

      }

      if (ratio) {
        val rest = 1 - sumOther;
        val jsStr = s"""{ "restItem": $rest}""";
        val js = Json.parse(jsStr);
        jsObjs = jsObjs ++ js;
      }

    } catch {
      case ex: Exception =>
        LogHelper.errLoger(s"Oryx2 getTagsDistribution make a err, ${ex.getMessage}\n");
        return json"""{}""";
    }

    return jsObjs;
  }
}

//
//分布
//json"""{
//商品id/标签标号：小数值
//... : ...
//其余：...
//}"""
//
//兴趣度
//json"""{
//商品id/标签标号：关联度（整数，越大关联度越高）
//... : ...
//}"""