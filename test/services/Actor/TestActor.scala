package services.Actor

import common.FileHelper.FileHelper
import common.FqueueHelper.FqueueHelper

/**
 * Created by horatio on 10/28/15.
 */
object TestActor {

  def main(args: Array[String]) {
    val fqueue = FqueueHelper.client()
    val msgs = FileHelper.readFile("./DynConfig/record.json")
    val map = FileHelper.readFile("./DynConfig/rules_13.bk")

    val trackQ = "Tracks_bash"
    val mapQ = "Maps_bash"

    var ret = true
    for (i <- 0 to 10) {
      ret = ret & fqueue.push(trackQ, msgs)
    }

    //ret = ret & fqueue.push(mapQ, map)
    if (ret == true) {
      println(LogActor.toString)
    }

//    val dynConfig = ConfigHelper.getConf()
//    val sceneIds = dynConfig.getString("Actor.Scene.sceneIds")
//    val separator = dynConfig.getString("Actor.Scene.separator")
//    val sceneIds = ConfigHelper.getMap(sceneIds, separator)
//    println(s"1234- $sceneIds")

//    println(fqueue.pull(trackQ).get)
//    println(s"${fqueue.pull(mapQ).get}")

  }

}
