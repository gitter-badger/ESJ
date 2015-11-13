package services.Actor

import common.FileHelper.FileHelper
import common.FqueueHelper.FqueueHelper
import play.api.libs.json.Json

/**
 * Created by horatio on 10/28/15.
 */
object Fq {

  def main(args: Array[String]) {
    val fqueue = FqueueHelper.client()
    val msgs = FileHelper.readFile("./DynConfig/record.json")
    val map = FileHelper.readFile("./DynConfig/rules_13.bk")

    val trackQ = "Tracks_bash"
    val mapQ = "Maps_bash"

    var ret = true
//    for (i <- 0 to 10) {
////      println(fqueue.pull(trackQ).get)
//      ret = ret & fqueue.push(trackQ, msgs)
//
//    }
//
//    //ret = ret & fqueue.push(mapQ, map)
//    if (ret == true) {
//      //println(s"${fqueue.pull(mapQ).get} ")
//      println(msgs)
//    }

    val m = Json.parse("""{"1":2, "3":4, "5":6}""").as[Map[String, Int]].par
    println(m)
    println(LogActor.toString)
  }

}
