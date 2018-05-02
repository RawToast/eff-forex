package forex.domain

import io.circe._
import io.circe.generic.extras.wrapped._

case class Price(value: BigDecimal) extends AnyVal
object Price {
  def apply(value: Integer): Price =
    Price(BigDecimal(value))

  def apply(value: Double): Price =
    Price(BigDecimal(s"$value"))

  implicit val encoder: Encoder[Price] = deriveUnwrappedEncoder[Price]
}
