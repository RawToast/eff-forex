package forex.services.oneforge

import java.time.OffsetDateTime

import cats.implicits.toShowOps
import forex.domain._
import org.atnos.eff._
import org.atnos.eff.addon.monix.task.{_task, fromTask}
import org.atnos.eff.writer._

object Interpreters {
  def live[R](client: RatesClient)(
    implicit
    m1: _task[R],
    m2: _writer[R],
    m3: _state[R]
  ): Algebra[Eff[R, ?]] = new MemoryCached[R](client)
}


final class MemoryCached[R] private[oneforge](client: RatesClient)(
  implicit
  m1: _task[R],
  m2: _writer[R],
  m3: _state[R]
) extends Algebra[Eff[R, ?]] {

  sealed trait Done

  object Done extends Done

  override def hasRateInCache(pair: Rate.Pair, expiryDateTime: OffsetDateTime): Eff[R, Boolean] =
    for {
      cache <- state.get[R, List[Rate]]
      inCache = cache.headOption.fold(false)(p => p.timestamp.value.isAfter(expiryDateTime))
      _ <- if (inCache) tell(s"Fresh value for Pair ${pair.show} found in cache") else tell(s"Pair ${pair.show} not found in cache")
    } yield inCache

  override def getCachedRate(pair: Rate.Pair): Eff[R, Option[Rate]] =
    for {
      cache <- state.get[R, List[Rate]]
      rate = cache.find(_.pair == pair)
    } yield rate

  override def getAllRates(): Eff[R, Either[Error, List[Rate]]] =
    fromTask(client.fetchRates)

  override def storeRates(rates: List[Rate]): Eff[R, Unit] =
    state.put[R, List[Rate]](rates)
}
