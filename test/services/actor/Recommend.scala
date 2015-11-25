package services.actor

import common.HBaseHelper.Row

/**
 * Created by horatio on 11/25/15.
 */
object Recommend {

  def rec(matches: Map[String, Map[String, String]], priority: Map[String, String]): Row ={

    Row("", "", Map[String, String]())
  }

  def main(args: Array[String]) {

  }
}
