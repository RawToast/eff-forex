package forex.services.oneforge

import java.time.OffsetDateTime

import forex.domain._

trait Algebra[F[_]] {
  def updateRates(expiryDateTime: OffsetDateTime): F[Error Either Unit]
  def get(pair: Rate.Pair): F[Error Either Rate]
  def getAll: F[Error Either List[Rate]]
}
