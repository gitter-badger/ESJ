package common

import play.api.libs.json.Json

import scala.util.{Failure, Success, Try}

/**
 * Created by horatio on 11/18/15.
 */
object Test {

  def main(args: Array[String]) {
    val track = Json.parse("""{"os": "linux"}""")
    Try(track \ "action") match {
      case Success(action) =>
        println(action.toString)
      case Failure(ex) =>
        println(s"$ex")
        //track.as[JsObject] ++= Json.obj(("action", "v"))
    }

    println(track)
    val sec = DateHelper.DateHelper.getCurrentTimeSeconds()
    println(sec)


  }
}
