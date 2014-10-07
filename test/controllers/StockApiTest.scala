package controllers

import akka.util.Timeout
import models.StockApi.StockSymbol
import org.scalatest.MustMatchers
import org.scalatestplus.play.PlaySpec
import org.specs2.mock.MockitoMocker
import play.api.Logger
import play.api.libs.json.{Json, JsValue}
import play.api.libs.ws.{WSRequestHolder, WSResponse}
import play.api.mvc.{Controller, Results}
import play.api.test.{FakeRequest, WithApplication}
import play.api.test.Helpers.contentAsJson

import scala.concurrent.Future
import scala.concurrent.duration._

class StockApiTest extends PlaySpec with MustMatchers with Results with MockitoMocker {

  class TestStockApi(response: JsValue) extends StockApi with Controller {

    override protected val logger = Logger(classOf[TestStockApi])

    override protected val SymbolsApiUrl = "SymbolsApiUrl"

    override protected val ChartDataApiUrl = "ChartDataApiUrl"

    override protected def callWS(url: String) = {
      val wsResponse = mock[WSResponse]
      when(wsResponse.json) thenReturn response

      val wsRequestHolder = mock[WSRequestHolder]
      when(wsRequestHolder.get()) thenReturn Future.successful(wsResponse)
      when(wsRequestHolder.withQueryString(any[(String, String)])) thenReturn wsRequestHolder

      wsRequestHolder
    }

  }

  implicit val timeout = Timeout(1.second)

  "A StockApi" when {

    "called on symbols" should {

      "yield a set of stock symbols when the third party service give a response" in new WithApplication() {
        val expected = Set(StockSymbol("Symbol", "Name"))

        val stockApi = new TestStockApi(Json.toJson(expected))
        val result = stockApi.symbols("").apply(FakeRequest())
        contentAsJson(result).as[Set[StockSymbol]] mustBe expected
      }

    }

  }

}
