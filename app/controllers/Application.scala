package controllers

import play.api.mvc.{Action, Controller}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

object Application extends Controller {

  def index() = Action.async {
    Future {
      Ok(views.html.index())
    }
  }

}
