package controllers

import play.api.mvc.{Action, Controller}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

trait Application { this: Controller =>

  def index() = Action.async {
    Future {
      Ok(views.html.index())
    }
  }

}

object Application extends Application with Controller
