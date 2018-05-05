package forex.processes.rates

import java.time.{OffsetDateTime, ZoneOffset}

import cats.Monad
import cats.data.EitherT
import cats.implicits._
import forex.domain._
import forex.services.{OneForge, oneforge}

object Processes {
  def apply[F[_]]: Processes[F] =
    new Processes[F]() {
      override val latestQuoteTime: () => OffsetDateTime =
        () => OffsetDateTime.now(ZoneOffset.UTC).minusSeconds(100L)
    }
}

trait Processes[F[_]] {

  import messages._

  val latestQuoteTime: () => OffsetDateTime

  def get(request: GetRequest)(
           implicit M: Monad[F],
           OneForge: OneForge[F]
         ): F[oneforge.Error Either Rate] = {

    val pair = Rate.Pair(request.from, request.to)

    def retrieve(pair: Rate.Pair): F[Either[oneforge.Error, Rate]] =
      OneForge.getCachedRate(pair)
      .map(_.toRight(forex.services.oneforge.Error.NotFound("No rates for currency pair.")))

    for {
      cached <- OneForge.hasRateInCache(pair, latestQuoteTime())
      rate <- if (cached) retrieve(pair)
              else EitherT(OneForge.getAllRates())
                      .semiflatMap(OneForge.storeRates)
                        .flatMapF(_ => retrieve(pair))
                          .value
    } yield rate
  }
}
