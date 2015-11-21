package services.business

import common.DateHelper.DateHelper

import scala.collection.parallel.immutable.{ParIterable, ParSeq}

/**
 * Created by horatio on 11/17/15.
 */
object ScenesTest {

  def main(args: Array[String]) {
    import scala.collection.mutable.{Map => muMap}
    import scala.collection.parallel.mutable.{ParMap => muParMap}
    val list = List.tabulate(100000)(i => i)
    val ls = (1 to 100000).map(i => i).toList
    val parIter = ParIterable.tabulate(100000)(i => i)
    val parSeq = ParSeq.tabulate(100000)(i => i)

    val mpm = muParMap[Int, Int]()
    val mm = muMap[Int, Int]()


    println(s"start get data ${DateHelper.getTimeOfDtae2Format}")

      ls map { i =>
        mm ++= muMap((i, i + 1))
      }

    println(s"end get data ${DateHelper.getTimeOfDtae2Format}")

    println(mm.last)
  }
}
