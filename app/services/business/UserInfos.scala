package services.business

import play.api.libs.json.JsValue

/**
 * Created by horatio on 10/30/15.
 */
object UserInfos {

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
