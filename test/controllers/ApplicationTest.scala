package controllers

import akka.util.Timeout
import org.scalatest.{MustMatchers, fixture}
import org.scalatestplus.play.PlaySpec
import play.api.mvc.{Controller, Results}
import play.api.test.Helpers.contentAsString
import play.api.test.{FakeRequest, WithApplication}

import scala.concurrent.duration._

class ApplicationTest extends PlaySpec with MustMatchers with Results {

  class TestApplication() extends Application with Controller

  implicit val timeout = Timeout(1.second)

  "An Application" when {

    "called on index" should {

      "yield the index view" in new WithApplication() {
        val application = new TestApplication()
        val result = application.index().apply(FakeRequest())

        contentAsString(result).contains("""<title>RealTime personalized stock stream</title>""") mustBe true
      }

    }

  }

}
