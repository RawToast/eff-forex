package forex.processes.rates

import java.time.{OffsetDateTime, ZoneOffset}

import cats.Monad
import cats.data.{EitherT, OptionT}
import cats.implicits._
import forex.domain._
import forex.services.OneForge

object Processes {
  def apply[F[_]]: Processes[F] =
    new Processes[F]() {
      override val latestQuoteTime: () => OffsetDateTime =
        () => OffsetDateTime.now(ZoneOffset.UTC).minusSeconds(100L)
    }
}

trait Processes[F[_]] {

  import converters._
  import messages._

  val latestQuoteTime: () => OffsetDateTime

  def get(request: GetRequest)(
           implicit M: Monad[F],
           OneForge: OneForge[F]
         ): F[ErrorMessage Either Rate] = {

    val pair = Rate.Pair(request.from, request.to)

    def retrieveFromCache(pair: Rate.Pair): F[Either[ErrorMessage, Rate]] =
      OptionT(OneForge.getCachedRate(pair))
          .toRight(forex.services.oneforge.Error.NotFound("No rates for currency pair."))
            .leftMap(toProcessError)
              .value

    for {
      cached <- OneForge.hasRateInCache(pair, latestQuoteTime())
      rate <- if (cached) retrieveFromCache(pair)
              else EitherT(OneForge.getAllRates())
                   .leftMap(toProcessError)
                   .semiflatMap(OneForge.storeRates)
                   .flatMapF(_ => retrieveFromCache(pair))
                   .value
    } yield rate
  }
}
