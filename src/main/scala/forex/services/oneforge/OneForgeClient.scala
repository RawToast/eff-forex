package forex.services.oneforge

import java.nio.ByteBuffer

import com.softwaremill.sttp.asynchttpclient.monix.AsyncHttpClientMonixBackend
import com.softwaremill.sttp.circe.asJson
import com.softwaremill.sttp.{sttp, _}
import forex.config.{ApplicationConfig, OneforgeConfig}
import forex.domain.{Price, Rate, Timestamp}
import monix.eval.Task
import monix.reactive.Observable
import org.zalando.grafter.macros.readerOf

final case class CurrencyPair(symbol: String, bid: Double, ask: Double, price: Double, timestamp: Long)

trait RatesClient {
  def fetchRates: Task[Either[Error, List[Rate]]]
}

@readerOf[ApplicationConfig]
case class OneForgeClient(oneforge: OneforgeConfig) extends RatesClient {

  import io.circe.generic.auto._

  private implicit lazy val monixBackend: SttpBackend[Task, Observable[ByteBuffer]] = AsyncHttpClientMonixBackend()

  def fetchRates: Task[Either[Error, List[Rate]]] = {
    lazy val url = s"${oneforge.baseurl}/${oneforge.apiversion}/quotes?api_key=${oneforge.apikey}"

    for {
      resp <- sttp.get(uri"$url")
              .response(asJson[List[CurrencyPair]])
              .send()
      bodyOrError = flattenResponse(resp)
      rates = bodyOrError.map(convertToPairs)
    } yield rates
  }

  private def flattenResponse[A, B](r: com.softwaremill.sttp.Response[Either[A, B]]): Either[Error, B] =
    r.body match {
      case Left(_) => Left(Error.Generic("Response had no body"))
      case Right(body) => body match {
        case Left(_) => Left(Error.Generic("Unable to parse body"))
        case Right(value) => Right(value)
      }
    }

  private def convertToPairs(currencyPairs: List[CurrencyPair]): List[Rate] = {
    def toRatePair(cp: CurrencyPair) = Rate.Pair.fromString(cp.symbol)

    currencyPairs.flatMap(cp => toRatePair(cp) match {
      case Some(value) => Some(Rate(value, Price(cp.price), Timestamp.from(cp.timestamp)))
      case None => None
    })
  }
}
