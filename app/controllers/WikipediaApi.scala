package controllers

import models.StockApi.StockSymbol
import play.api.Play.current
import play.api.libs.json.JsError
import play.api.libs.ws.WS
import play.api.mvc.{Action, Controller}
import play.api.{Logger, Play, Routes}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

object WikipediaApi extends Controller {

  private val logger = Logger(WikipediaApi.getClass)

  private val WikipediaApiPageSearchUrl: String = Play.configuration.getString("wikipedia-api.lookup").get

  private val WikipediaApiPageGetUrl: String = Play.configuration.getString("wikipedia-api.page").get

  def vcard() = Action.async(parse.json) { request =>
    request.body.validate[StockSymbol] map { symbol =>
      WS.url(WikipediaApiPageSearchUrl).withQueryString("titles" -> s"${symbol.Symbol}|${symbol.Name}|${symbol.Name.split(' ').head}").get().flatMap { response =>
        (response.json \ "query" \\ "pageid").head.validate[Int].map { pageId =>
          WS.url(WikipediaApiPageGetUrl).withQueryString("pageids" -> pageId.toString).get().flatMap { pageResponse =>
            (pageResponse.json \ "query" \ "pages" \ pageId.toString \ "fullurl").validate[String].map { url =>
              WS.url(url).get().map { page => Ok(page.body) }
            } recoverTotal { e =>
              Future.successful(BadRequest(JsError.toFlatJson(e)))
            }
          }
        } recoverTotal { e =>
          Future.successful(BadRequest(JsError.toFlatJson(e)))
        }
      }
    } recoverTotal { e =>
      Future.successful(BadRequest(JsError.toFlatJson(e)))
    }
  }

  def javascriptRoutes() = Action.async { implicit request =>
    Future.successful {
      Ok {
        Routes.javascriptRouter("wikipediaApiJavascriptRoutes") (
          routes.javascript.WikipediaApi.vcard
        )
      }
    }
  }

}
