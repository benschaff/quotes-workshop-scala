package controllers

import models.StockApi.{ChartRequest, ChartRequestElement, StockSymbol}
import play.api.Play.current
import play.api.cache.Cache
import play.api.libs.json.{JsError, JsValue, Json}
import play.api.libs.ws.{WSRequestHolder, WS}
import play.api.mvc.{Action, Controller}
import play.api.{Logger, Play, Routes}

import scala.concurrent.Future
import scala.concurrent.duration._

import scala.concurrent.ExecutionContext.Implicits.global

trait StockApi { this: Controller =>

  protected val logger: Logger

  protected val SymbolsApiUrl: String

  protected val ChartDataApiUrl: String

  def symbols(query: String) = Action.async {
    callWS(SymbolsApiUrl).withQueryString("input" -> query).get map { response =>
      import models.StockApi.stockSymbolJsonFormat

      response.json.validate[Set[StockSymbol]] map { symbols =>
        Ok(Json.toJson(symbols))
      } recoverTotal { e =>
        BadRequest(JsError.toFlatJson(e))
      }
    } recover {
      case t: Throwable =>
        logger.warn(s"Failed to query: $SymbolsApiUrl with $query", t)

        Ok(Json.toJson(Set.empty[StockSymbol]))
    }
  }

  def last30Days(symbol: String) = Action.async {
    val data = Cache.getAs[JsValue](s"$symbol.chartData.last30Days")
    if (data.isDefined) Future.successful(Ok(data.get))
    else {
      import models.StockApi.chartRequestJsonFormat

      callWS(ChartDataApiUrl).withQueryString {
        "parameters" -> Json.stringify(Json.toJson(ChartRequest(elements = List(ChartRequestElement(symbol = symbol)))))
      }.get map {
        response =>
          Cache.set(s"$symbol.chartData.last30Days", response.json, 24.hour)

          Ok(response.json)
      }
    }
  }

  def javascriptRoutes() = Action.async { implicit request =>
    Future.successful {
      Ok {
        Routes.javascriptRouter("stockApiJavascriptRoutes") (
          routes.javascript.StockApi.symbols,
          routes.javascript.StockApi.last30Days
        )
      }
    }
  }

  protected def callWS(url: String): WSRequestHolder

}

object StockApi extends StockApi with Controller {

  override protected val logger = Logger(classOf[StockApi])

  override protected val SymbolsApiUrl = Play.configuration.getString("markitondemand-api.lookup").get

  override protected val ChartDataApiUrl = Play.configuration.getString("markitondemand-api.chartData").get

  override protected def callWS(url: String) = WS.url(url)

}
