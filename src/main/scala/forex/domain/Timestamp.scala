package forex.domain

import io.circe._
import io.circe.generic.extras.wrapped._
import io.circe.java8.time._

import java.time.{LocalDateTime, OffsetDateTime, ZoneOffset}

case class Timestamp(value: OffsetDateTime) extends AnyVal

object Timestamp {
  def now: Timestamp =
    Timestamp(OffsetDateTime.now)

  def from(millisSinceEpoch: Long) =
    Timestamp(
      OffsetDateTime.of(
        LocalDateTime.ofEpochSecond(millisSinceEpoch,0 , ZoneOffset.UTC),  ZoneOffset.UTC))

  implicit val encoder: Encoder[Timestamp] =
    deriveUnwrappedEncoder[Timestamp]
}
