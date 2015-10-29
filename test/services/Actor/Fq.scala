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
    val msgs = FileHelper.readFile("/home/horatio/big-data/track_json/record.json")

    val js = Json.parse("""{"1":{"1":"1"}, "2":{"2":"2"}}""")
    val queue = "Tracks_bash"

    var ret = true
    for (i <- 0 to 9) {
       ret = ret & fqueue.push(queue, msgs)
      println(Json.parse(msgs))
    }
    if (ret == true) println("push to fq ")

//    Thread.sleep(5000)
//    println(fqCli.pull(queue))

  }

}
