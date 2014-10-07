package views

import akka.util.Timeout
import org.scalatestplus.play.PlaySpec
import play.api.test.Helpers.contentAsString

import scala.concurrent.duration._

class IndexViewTest extends PlaySpec {

  implicit val timeout = Timeout(1.second)

  "An Index View" when {

    "rendered" should {

      "include the title: RealTime personalized stock stream" in new App {
        val html = views.html.index()

        contentAsString(html) must include ("<title>RealTime personalized stock stream</title>")
      }

    }

  }

}
