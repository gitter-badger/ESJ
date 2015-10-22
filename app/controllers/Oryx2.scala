package controllers

import business.DataManager.ServingLayer
import common.LogHelper.LogHelper
import common.safety.{APISecured, APIToken, AuthTools}
import play.api.mvc.Controller
import rapture.json.formatters.compact
import rapture.json.jsonBackends.lift._
import rapture.json.{Json, _}

object Oryx2 extends Controller with APISecured {
  def getItemInterestsByUid(uid: String, num: String) = withAuthAsync { apiUser =>
    implicit request =>
      try{
        var status = 0;
        val tokens = APIToken.getRequestVerifToken(request);
        var jsonObj = json"""{}""";

        if (tokens == null) {
          Ok("""{}""");
        } else {
          val ts = tokens("d");

          AuthTools.auth(apiUser.username, ts, apiUser.password, tokens("p")) match {
            case true => status = 1;
            case false => status = 0;
          }

          if (status == 1) {
            val ret = ServingLayer.getItemInterestsByUid(uid,num);
            Ok(Json.format(ret));
          }else {
            Ok("""{}""");
          }
        }
      }catch {
        case ex: Exception =>
          LogHelper.errLoger(s"Oryx2 getItemInterestsByUid make a err, ${ex.getMessage}\n");
          Ok("""{}""");
      }

  }

  def getTagInterestsByUid(uid: String, num: String) = withAuthAsync { apiUser =>
    implicit request =>
      try{
        var status = 0;
        val tokens = APIToken.getRequestVerifToken(request);
        var jsonObj = json"""{}""";

        if (tokens == null) {
          Ok("""{}""");
        } else {
          val ts = tokens("d");

          AuthTools.auth(apiUser.username, ts, apiUser.password, tokens("p")) match {
            case true => status = 1;
            case false => status = 0;
          }

          if (status == 1) {
            val ret = ServingLayer.getTagInterestsByUid(uid,num);
            Ok(Json.format(ret));
          }else {
            Ok("""{}""");
          }
        }
      }catch {
        case ex: Exception =>
          LogHelper.errLoger(s"Oryx2 getTagInterestsByUid make a err, ${ex.getMessage}\n");
          Ok("""{}""");
      }
  }

  def getItemsDistribution(num: String, ratio: Boolean) = withAuthAsync { apiUser =>
    implicit request =>
      try{
        var status = 0;
        val tokens = APIToken.getRequestVerifToken(request);
        var jsonObj = json"""{}""";

        if (tokens == null) {
          Ok("""{}""");
        } else {
          val ts = tokens("d");

          AuthTools.auth(apiUser.username, ts, apiUser.password, tokens("p")) match {
            case true => status = 1;
            case false => status = 0;
          }

          if (status == 1) {
            val ret = ServingLayer.getItemsDistribution(num, ratio);
            Ok(Json.format(ret));
          }else {
            Ok("""{}""");
          }
        }
      }catch {
        case ex: Exception =>
          LogHelper.errLoger(s"Oryx2 getItemsDistribution make a err, ${ex.getMessage}\n");
          Ok("""{}""");
      }
  }

  def getTagsDistribution(num: String, ratio: Boolean) = withAuthAsync { apiUser =>
    implicit request =>
      try{
        var status = 0;
        val tokens = APIToken.getRequestVerifToken(request);
        var jsonObj = json"""{}""";

        if (tokens == null) {
          Ok("""{}""");
        } else {
          val ts = tokens("d");

          AuthTools.auth(apiUser.username, ts, apiUser.password, tokens("p")) match {
            case true => status = 1;
            case false => status = 0;
          }

          if (status == 1) {
            val ret = ServingLayer.getTagsDistribution(num, ratio);
            Ok(Json.format(ret));
          }else {
            Ok("""{}""");
          }
        }
      }catch {
        case ex: Exception =>
          LogHelper.errLoger(s"Oryx2 getTagsDistribution make a err, ${ex.getMessage}\n");
          Ok("""{}""");
      }
  }

}

