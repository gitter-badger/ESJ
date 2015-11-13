package common.HBaseHelper

import scala.collection.parallel.ParIterable
import scala.collection.parallel.immutable.ParMap

case class Row(key: String, family: String, qualifersAndValues: Map[String,String])

object HBaseHelper {

  def getRows(table: String, key: ParIterable[String]): ParMap[String, Row] = {
    import scala.collection.mutable.{Map => muMap}
    val rows = muMap[String, Row]()

    rows.toMap.par
  }
}
