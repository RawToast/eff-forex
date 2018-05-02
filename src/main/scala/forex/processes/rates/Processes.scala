package forex.processes.rates

import java.time.{OffsetDateTime, ZoneOffset}

import cats.Monad
import cats.data.EitherT
import forex.domain._
import forex.services._

object Processes {
  def apply[F[_]]: Processes[F] =
    new Processes[F]() {
      override val latestQuoteTime: () => OffsetDateTime =
        () => OffsetDateTime.now(ZoneOffset.UTC).minusSeconds(100L)
    }
}

trait Processes[F[_]] {
  import messages._
  import converters._

  val latestQuoteTime: () => OffsetDateTime

  def get(
      request: GetRequest
  )(
      implicit
      M: Monad[F],
      OneForge: OneForge[F]
  ): F[Error Either Rate] =
    (for {
      _ <- EitherT(OneForge.updateRates(latestQuoteTime())).leftMap(toProcessError)
      result ← EitherT(OneForge.get(Rate.Pair(request.from, request.to))).leftMap(toProcessError)
    } yield result
  ).value

  def getAll(
      request: GetRequest
    )(
       implicit
       M: Monad[F],
       OneForge: OneForge[F]
    ): F[Error Either List[Rate]] =
      (for {
        _ <- EitherT(OneForge.updateRates(latestQuoteTime())).leftMap(toProcessError)
        result ← EitherT(OneForge.getAll).leftMap(toProcessError)
      } yield result
    ).value
}
