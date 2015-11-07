package services.Actor

import common.FileHelper.FileHelper
import common.FqueueHelper.FqueueHelper

/**
 * Created by horatio on 10/28/15.
 */
object Fq {

  def main(args: Array[String]) {
    val fqueue = FqueueHelper.client()
    val msgs = FileHelper.readFile("/home/horatio/big-data/track_json/record.json")
    val map = FileHelper.readFile("/home/horatio/big-data/maps/rules.map")

    val trackQ = "Tracks_bash"
    val mapQ = "Maps_bash"

    var ret = true
    for (i <- 0 to 20) {
       ret = ret & fqueue.push(trackQ, msgs)
    }

    ret = ret & fqueue.push(mapQ, map)

    if (ret == true) println(s"${fqueue.pull(mapQ).get} ")

  }

}
