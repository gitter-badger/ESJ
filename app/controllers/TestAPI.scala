package controllers

import common.FileHelper.FileHelper
import common.HttpHelper.HttpHelper
import common.safety.Encrypt
import config.DynConfiguration
import controllers.Oryx2._
import play.api.libs.json.Json
import play.api.mvc.Action
import services.ESJ

object TestAPI {
  def oryx2 = Action {
    val hi = scala.collection.mutable.Map[String, String]()
    val tsEn = Encrypt.getEncryptedTs();
    val dataEn = Encrypt.getEncryptData("bodao@qq.com", tsEn, "BOdao2015*");
    val distNum = 5
    val intstNum = 10
    val bool = 0

    hi += ("d" -> tsEn);
    hi += ("u" -> "bodao@qq.com");
    hi += ("p" -> dataEn);

    var ret = HttpHelper.httpGetRequest(s"http://hadoop07:19999/Oryx2/getTagsDistribution/${distNum}/${bool}", hi);
    println(s"tagsDis ---- ${ret}\n--------------------------------\n");
    ret = HttpHelper.httpGetRequest(s"http://hadoop07:19999/Oryx2/getTagInterestsByUid/35a1b6f0509bf9f4/${intstNum}", hi);
    println(s"tagInt ---- ${ret}\n--------------------------------\n");

    ret = HttpHelper.httpGetRequest(s"http://hadoop07:19999/Oryx2/getItemInterestsByUid/35a1b6f0509bf9f4/${intstNum}", hi);
    println(s"itemInt ---- ${ret}\n--------------------------------\n");
    ret = HttpHelper.httpGetRequest(s"http://hadoop07:19999/Oryx2/getItemsDistribution/${distNum}/${bool}", hi);
    println(s"itemsDis ---- ${ret}\n--------------------------------\n");

    Ok(ret("page"));
  }

  def esj = Action {
    val dynconf = DynConfiguration.getConf()
    val redisQueueKey = dynconf.getString("Redis.QueueKey")

    val raw = FileHelper.readFile("/home/horatio/big-data/track_json/W23.json")
    ESJ.judgeScene(Json.parse(raw))

    Ok("ok!")
  }

  val dynconf = DynConfiguration.getConf()
  //val busiName = dynconf.getString("Business.Name")
  val redisQueueKey = dynconf.getString("Redis.QueueKey")

  def redis = Action {


    Ok("ok!!!")
  }
}
