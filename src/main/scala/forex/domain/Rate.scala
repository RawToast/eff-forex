package forex.domain

import cats.Show
import cats.implicits.toShowOps
import io.circe._
import io.circe.generic.semiauto._

case class Rate(
    pair: Rate.Pair,
    price: Price,
    timestamp: Timestamp
)

object Rate {
  final case class Pair(
      from: Currency,
      to: Currency
  )

  object Pair {
    def fromString(pairStr: String): Option[Pair] = {
      if (pairStr.length != 6) None
      else {
        for {
          c1 <- Currency.optFromString(pairStr.take(3))
          c2 <- Currency.optFromString(pairStr.drop(3))
        } yield Pair(c1, c2)
      }
    }

    implicit val encoder: Encoder[Pair] =
      deriveEncoder[Pair]

    implicit val show: Show[Pair] = Show.show[Pair](pair => pair.from.show + pair.to.show)
  }

  implicit val encoder: Encoder[Rate] =
    deriveEncoder[Rate]
}
