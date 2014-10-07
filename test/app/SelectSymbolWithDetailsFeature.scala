package app

import org.scalatestplus.play._
import play.api.libs.json.Json
import play.api.mvc.{Action, Results}
import play.api.test.FakeApplication

class SelectSymbolWithDetailsFeature extends PlaySpec with OneServerPerSuite with OneBrowserPerTest with FirefoxFactory {

  private val MarkitonDemandApiChartDataResponse =
    """
      {
      |   "Labels":null,
      |   "Positions":[
      |      0,
      |      0.05,
      |      0.1,
      |      0.15,
      |      0.2,
      |      0.25,
      |      0.3,
      |      0.35,
      |      0.4,
      |      0.45,
      |      0.5,
      |      0.55,
      |      0.6,
      |      0.65,
      |      0.7,
      |      0.75,
      |      0.8,
      |      0.85,
      |      0.9,
      |      0.95,
      |      1
      |   ],
      |   "Dates":[
      |      "2014-09-08T00:00:00",
      |      "2014-09-09T00:00:00",
      |      "2014-09-10T00:00:00",
      |      "2014-09-11T00:00:00",
      |      "2014-09-12T00:00:00",
      |      "2014-09-15T00:00:00",
      |      "2014-09-16T00:00:00",
      |      "2014-09-17T00:00:00",
      |      "2014-09-18T00:00:00",
      |      "2014-09-19T00:00:00",
      |      "2014-09-22T00:00:00",
      |      "2014-09-23T00:00:00",
      |      "2014-09-24T00:00:00",
      |      "2014-09-25T00:00:00",
      |      "2014-09-26T00:00:00",
      |      "2014-09-29T00:00:00",
      |      "2014-09-30T00:00:00",
      |      "2014-10-01T00:00:00",
      |      "2014-10-02T00:00:00",
      |      "2014-10-03T00:00:00",
      |      "2014-10-06T00:00:00"
      |   ],
      |   "Elements":[
      |      {
      |         "Currency":"USD",
      |         "TimeStamp":null,
      |         "Symbol":"GOOGL",
      |         "Type":"price",
      |         "DataSeries":{
      |            "close":{
      |               "min":579.63,
      |               "max":605.4,
      |               "maxDate":"2014-09-19T00:00:00",
      |               "minDate":"2014-10-01T00:00:00",
      |               "values":[
      |                  601.63,
      |                  591.97,
      |                  593.42,
      |                  591.11,
      |                  584.9,
      |                  581.64,
      |                  588.78,
      |                  593.29,
      |                  597.27,
      |                  605.4,
      |                  597.27,
      |                  591.18,
      |                  598.42,
      |                  585.25,
      |                  587.9,
      |                  587.81,
      |                  588.41,
      |                  579.63,
      |                  580.88,
      |                  586.25,
      |                  587.48
      |               ]
      |            }
      |         }
      |      }
      |   ]
      |}
    """.stripMargin

  private val WikipediaApiLookupResponse =
    """
      |{
      |   "query":{
      |      "pages":{
      |         "1092923":{
      |            "pageid":1092923,
      |            "ns":0,
      |            "title":"Google"
      |         },
      |         "1093108":{
      |            "pageid":1093108,
      |            "ns":0,
      |            "title":"GOOG"
      |         },
      |         "2236944":{
      |            "pageid":2236944,
      |            "ns":0,
      |            "title":"Google Inc"
      |         }
      |      }
      |   }
      |}
    """.stripMargin

  private val WikipediaApiPageResponse =
    """
      |{
      |   "query":{
      |      "pages":{
      |         "1092923":{
      |            "pageid":1092923,
      |            "ns":0,
      |            "title":"Google",
      |            "contentmodel":"wikitext",
      |            "pagelanguage":"en",
      |            "touched":"2014-10-04T09:26:28Z",
      |            "lastrevid":627302270,
      |            "counter":"",
      |            "length":132628,
      |            "fullurl":"http://en.wikipedia.org/wiki/Google",
      |            "editurl":"http://en.wikipedia.org/w/index.php?title=Google&amp;action=edit",
      |            "canonicalurl":"http://en.wikipedia.org/wiki/Google"
      |         }
      |      }
      |   }
      |}
    """.stripMargin

  implicit override lazy val app: FakeApplication =
    FakeApplication(
      additionalConfiguration = Map(
        "markitondemand-api.chartData" -> s"http://localhost:$port/Api/v2/InteractiveChart/json",
        "wikipedia-api.lookup" -> s"http://localhost:$port/w/lookup",
        "wikipedia-api.page" -> s"http://localhost:$port/w/page"
      ),
      withRoutes = {
        case ("GET", "/Api/v2/InteractiveChart/json") => Action(Results.Ok(Json.parse(MarkitonDemandApiChartDataResponse)).as("application/json"))
        case ("GET", "/w/lookup") => Action(Results.Ok(Json.parse(WikipediaApiLookupResponse)).as("application/json"))
        case ("GET", "/w/page") => Action(Results.Ok(Json.parse(WikipediaApiPageResponse)).as("application/json"))
      }
    )

  "The select symbol with details feature" must {
    s"support browsing with Chrome" in {
      go to s"http://localhost:$port"
      pageTitle mustBe "RealTime personalized stock stream"
      eventually {
        pageSource must include ("value=\"currentSymbolChartData\"")
        pageSource must include regex "<caption.*>Google Inc\\.</caption>"
      }
    }
  }

}
