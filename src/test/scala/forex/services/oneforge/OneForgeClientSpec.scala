package forex.services.oneforge

import com.github.tomakehurst.wiremock._
import com.github.tomakehurst.wiremock.client.WireMock
import com.github.tomakehurst.wiremock.client.WireMock._
import forex.config._
import forex.domain.Rate
import monix.execution.Scheduler.Implicits.global
import org.scalatest.{AsyncFreeSpec, BeforeAndAfterEach}

import scala.concurrent.Future

class OneForgeClientSpec extends AsyncFreeSpec with BeforeAndAfterEach {

  private val testport = 9995
  private val server = new WireMockServer(testport)

  override def beforeEach(): Unit = {
    server.start()
    WireMock.configureFor(testport)
  }

  override def afterEach(): Unit = {
    server.resetAll()
    server.stop()
  }

  private val testConfig = OneforgeConfig("key", "1.0.0", s"http://localhost:$testport")
  private val underTest = OneForgeClient(testConfig)

  lazy val validRatesResponse =
    """[
      |    {
      |        "symbol": "JPYUSD",
      |        "bid": 0.00915902,
      |        "ask": 0.00915902,
      |        "price": 0.00915902,
      |        "timestamp": 1525109103
      |    }
      |]""".stripMargin

  "OneForgeClient" - {

    "When the service returns successfully" - {

      "Should parse the response into Rates" in {

        stubResponse(validRatesResponse)

        val response: Future[Either[Error, List[Rate]]] =
          underTest.fetchRates.runAsync

        response.map {
          case Right(value) => assert(value.nonEmpty, "Did not return any rates")
          case Left(_) =>  fail("Did not response successfully")
        }
      }

      "Should return an error if the message body is invalid" in {

        stubResponse("nonsense")

        val response: Future[Either[Error, List[Rate]]] =
          underTest.fetchRates.runAsync

        response.map {
          case Right(_) => fail("Did not return an error")
          case Left(_) =>  succeed
        }
      }
    }

    "When the service returns unsuccessfully" - {

      "Should return an error" in {

        stubResponse("Error", status = 500)

        val response: Future[Either[Error, List[Rate]]] =
          underTest.fetchRates.runAsync

        response.map {
          case Right(_) => fail("Did not return an error")
          case Left(_) => succeed
        }
      }
    }
  }

  private def stubResponse(body: String, status: Int = 200) =
    stubFor(get(urlEqualTo("/1.0.0/quotes?api_key=key"))
            .willReturn(aResponse()
              .withStatus(status)
              .withHeader("Content-Type", "application/json")
              .withBody(body)))

}
