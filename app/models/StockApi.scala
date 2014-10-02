package models

import play.api.libs.json.Json

object StockApi {

  case class StockSymbol(Symbol: String, Name: String)

  case class ChartRequestElement(symbol: String, value: String = "price", params: List[String] = List("c"))

  case class ChartRequest(normalized: Boolean = false, numberOfDays: Int = 30, dataPeriod: String = "Day", elements: List[ChartRequestElement] = List())

  implicit val stockSymbolJsonFormat = Json.format[StockSymbol]

  implicit val chartRequestElementJsonFormat = Json.format[ChartRequestElement]

  implicit val chartRequestJsonFormat = Json.format[ChartRequest]

}
