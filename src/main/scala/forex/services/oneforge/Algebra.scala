package forex.services.oneforge

import java.time.OffsetDateTime

import forex.domain._

trait Algebra[F[_]] {
  def hasRateInCache(pair: Rate.Pair, expiryDateTime: OffsetDateTime): F[Boolean]

  def getCachedRate(pair: Rate.Pair): F[Option[Rate]]

  def getAllRates(): F[Error Either List[Rate]]

  def storeRates(rates: List[Rate]): F[Unit]
}
