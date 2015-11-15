package services.business

import play.api.libs.json.JsValue

/**
 * Created by horatio on 10/30/15.
 */
object Triggers {
  val passBit = '0'
  val terms = Array("Age", "Area", "Gender", "Salary")
  val meanings = Map[String, String]()
  val indexes = Map[String, Int]()
  val separator = "-"

  def judgeVariables(code: String, variables: Map[String, String], features: Map[String, String]): Boolean = {

    variables.keys.par foreach { variable =>
      indexes.get(variable) match {
        case Some(index) =>
          if (code.charAt(index) != passBit) {
            /**
             * Theoretically, if "bit != passBit", value must exist!
             * Just preventing unseen accidents
             */
            val value = variables.get(variable).get
            features.get(variable) match {
              case Some(feature) =>
                if (!judge(value, feature, variable)) return false

              case None =>
              /**
               * TYPICALLY, assume those without specific "feature" value pass!!!
               */
            }
          }

        case None =>
        /**
         * TYPICALLY, assume variable without "index" in indexes map make all pass!!!
         * Indexs syncanization delay may lead to it when businesses add a variable and its code.
         */
      }

    }
    true
  }

  private def judge(variable: String, value: String, feature: String): Boolean ={

    meanings.get(variable) match {
      case Some(meaning) => meaning match {
        case "Range" =>
          val range = value.split(separator, 2)
          /**
           * range sholud be 2 in length, thus invalid value of variable
           * if "feature" not in range, return false
           */
          if (range.length == 2) {
            val min = range.apply(0)
            val max = range.apply(1)
            if (feature > max || feature <= min) return false
          }
        case "Value" => if (feature != value) return false
      }

      case None =>
      /**
       * TYPICALLY, assume "variable" without specific type pass!!!
       * Indexs syncanization delay may lead to it when businesses add a variable and its code.
       */
    }

    true
  }

}

class Triggers {

  val passBit = '0'
  val terms = Array("Age", "Area", "Gender", "Salary")
  private def judge(bit: Char, term: String, feature: Map[String, String], variables: JsValue): Boolean = {
    /**
     * Theoretically, if bit != trueBit, value must exist!
     * Just preventing unseen accidents
     */
    if (bit != passBit)
      feature.get(term) match {
        case Some(value) =>
          if (value != (variables \ term).as[String]) return false
        case None =>
          println("")
      }
    true
  }


  /***** need optimizing *****/
  def judgeVariables(features: Map[String, String], code: String, variables: JsValue): Boolean = {

    val indexs = Map[String, Int]()
    terms.par.foreach { feature =>
      val index = indexs.get(feature).get
      if (!judge(code.charAt(index), feature, features, variables)) return false
    }

    true
  }

  def judgeGender(infos: JsValue, opt: String): String = {

    if (opt != "0") {
      val gender = (infos \ "gender").as[String]
      opt match {
        case "1" =>
          if (gender == "female") opt
          else ""
        case "2" =>
          if (gender == "male") opt
          else ""
        case _ => ""
      }
    } else opt
  }

  def judgeAge(infos: JsValue, opt: String): String = {

    if (opt != "0") {
      val age = (infos \ "age").as[Int]
      opt match {
        case "1" =>
          if (age <= 15) opt
          else ""
        case "2" =>
          if (age >= 16 && age <= 25) opt
          else ""
        case "3" =>
          if (age >= 26 && age <= 35) opt
          else ""
        case "4" =>
          if (age >= 36 && age <= 45) opt
          else ""
        case "5" =>
          if (age >= 46 && age <= 55) opt
          else ""
        case "6" =>
          if (age >= 56) opt
          else ""
        case _ => ""
      }
    } else opt
  }

  def judgeArea(infos: JsValue, opt: String): String = {
    if (opt != "0") {
      val area = (infos \ "area").as[String]
      opt match {
        case "1" =>
          if (area == "south") opt
          else ""
        case "2" =>
          if (area == "central") opt
          else ""
        case "3" =>
          if (area == "east") opt
          else ""
        case "4" =>
          if (area == "north") opt
          else ""
        case "5" =>
          if (area == "northeast") opt
          else ""
        case "6" =>
          if (area == "northwest") opt
          else ""
        case "7" =>
          if (area == "sorthwest") opt
          else ""
        case _ => ""
      }
    } else opt
  }

  def judgeSalary(infos: JsValue, opt: String): String = {
    if (opt != "0") {
      val salary = (infos \ "salary").as[Int]
      opt match {
        case "1" =>
          if (salary <= 3500) opt
          else ""
        case "2" =>
          if (salary > 3500 && salary <= 5000) opt
          else ""
        case "3" =>
          if (salary > 5000 && salary <= 8000) opt
          else ""
        case "4" =>
          if (salary > 8000 && salary <= 12500) opt
          else ""
        case "5" =>
          if (salary >= 12500) opt
          else ""
        case _ => ""
      }
    } else opt
  }
}