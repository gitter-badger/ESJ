package controllers

import play.api.mvc.{Action, Controller}

/**
 * Created by horatio on 10/26/15.
 */

class Application extends Controller {
  def index = Action {
    Ok(views.html.index("Your new application is ready."))
  }
}

object Application extends Controller {
  def index = Action {
    Ok(views.html.index("Your new application is ready."))
  }
}