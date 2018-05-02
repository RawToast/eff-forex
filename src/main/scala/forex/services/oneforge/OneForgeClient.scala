package forex.services.oneforge

import java.nio.ByteBuffer

import com.softwaremill.sttp.asynchttpclient.monix.AsyncHttpClientMonixBackend
import com.softwaremill.sttp.circe.asJson
import com.softwaremill.sttp.{sttp, _}
import forex.domain.{Price, Rate, Timestamp}
import monix.eval.Task
import monix.reactive.Observable

class OneForgeClient {

  import cats.syntax.show._
  import io.circe.generic.auto._

  implicit val monixBackend: SttpBackend[Task, Observable[ByteBuffer]] = AsyncHttpClientMonixBackend()

  final case class CurrencyPair(symbol: String, bid: Double, ask: Double, price: Double, timestamp: Long)

  def fetchRate(pair: Rate.Pair): Task[Either[Error, Rate]] = {
    val key = "TUQ8fBGuT2h83ZFyuRhlhI99I0g2Yqkz"
    val fxpair = pair.from.show ++ pair.to.show
    val url = s"https://forex.1forge.com/1.0.3/quotes?pairs=$fxpair&api_key=$key"

    for {
      resp <- sttp.get(uri"$url")
              .response(asJson[List[CurrencyPair]])
              .send()
      body = bodyOrError(resp)
      // TODO use headOpt and move to left if there are no prices
      singlePair = body.map(_.head)
    } yield singlePair.map(cp => Rate(pair, Price(cp.price), Timestamp.from(cp.timestamp)))
  }

  private def bodyOrError[A, B](r: com.softwaremill.sttp.Response[Either[A, B]]): Either[Error, B] = {
    r.body match {
      case Left(_) => Left(Error.Generic("Response had no body"))
      case Right(body) => body match {
        case Left(_) => Left(Error.Generic("Unable to parse body"))
        case Right(value) => Right(value)
      }
    }
  }
}
