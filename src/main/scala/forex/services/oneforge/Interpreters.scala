package forex.services.oneforge

import java.time.OffsetDateTime

import cats.implicits.toShowOps
import forex.domain._
import forex.services._writer
import monix.eval.Task
import org.atnos.eff._
import org.atnos.eff.either._
import org.atnos.eff.writer._
import org.atnos.eff.addon.monix.task.{_task, fromTask}
import scala.collection.concurrent.TrieMap


object Interpreters {

  def live[R](client: RatesClient)(
    implicit
    m1: _task[R],
    m2: _writer[R]
  ): Algebra[Eff[R, ?]] = new MemoryCached[R](client)
}



final class MemoryCached[R] private[oneforge](client: RatesClient)(
                                                                implicit
                                                                m1: _task[R],
                                                                m2: _writer[R]
                                                              ) extends Algebra[Eff[R, ?]] {

  sealed trait Done

  object Done extends Done

  val cache: TrieMap[Rate.Pair, Rate] = new TrieMap()

  override def get(
                    pair: Rate.Pair
                  ): Eff[R, Error Either Rate] = {
    for {
      _ <- tell(s"Fetching rates for ${pair.show}")
      rate <- fromTask(Task(fromCache(pair)))
    } yield rate
  }

  override def updateRates(cacheExpiryDateTime: OffsetDateTime): Eff[R, Either[Error, Unit]] =
    for {
      result <- fromTask(updateCache(cacheExpiryDateTime))
    } yield result

  override def getAll: Eff[R, Either[Error, List[Rate]]] =
    fromTask(client.fetchRates)

  private def updateCache(cacheExpiryDateTime: OffsetDateTime): Task[Either[Error, Unit]] =
      checkCache(cacheExpiryDateTime) match {
      case Right(_) => Task(Right(Unit))
      case Left(_) => client.fetchRates.map(_.map(updateCache))
    }

  private def checkCache(cacheExpiryDateTime: OffsetDateTime): Either[Error, Done] =
    cache
      .headOption
      .toRight(Error.NotFound("No rates for currency pair."))
      .map(_._2.timestamp.value.isAfter(cacheExpiryDateTime))
      .flatMap(if(_) Right(Done) else Left(Error.NotFound("No rates found")))

  private def updateCache(rs: List[Rate]): Unit = {
    state.modify((_: Map[Rate.Pair, Any]) => rs.foldLeft(Map[Rate.Pair, Rate]()) { (m, s) => m.updated(s.pair, s) })
    rs.foreach(r => cache.update(r.pair, r))
  }

  private def fromCache(pair: Rate.Pair) =
    cache.get(pair)
      .toRight(Error.NotFound("No rates for currency pair."))
}
