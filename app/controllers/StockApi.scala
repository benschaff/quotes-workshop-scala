package controllers

import models.StockApi.{ChartRequestElement, ChartRequest, StockSymbol}
import play.api.Play.current
import play.api.cache.Cache
import play.api.libs.json.{JsValue, JsError, Json}
import play.api.libs.ws.WS
import play.api.mvc.{Action, Controller}
import play.api.{Routes, Logger, Play}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.concurrent.duration._

object StockApi extends Controller {

  private val logger = Logger(StockApi.getClass)

  private val SymbolsApiUrl: String = Play.configuration.getString("markitondemand-api.lookup").get

  private val ChartDataApiUrl: String = Play.configuration.getString("markitondemand-api.chartData").get

  def symbols(query: String) = Action.async {
    WS.url(SymbolsApiUrl).withQueryString("input" -> query).get() map { response =>
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

      WS.url(ChartDataApiUrl).withQueryString {
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

}
