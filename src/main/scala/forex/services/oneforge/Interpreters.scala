package forex.services.oneforge

import forex.domain._
import monix.eval.Task
import org.atnos.eff._
import org.atnos.eff.addon.monix.task.{_task, fromTask}

object Interpreters {
  def dummy[R](
                implicit
                m1: _task[R]
              ): Algebra[Eff[R, ?]] = new Dummy[R]

  def live[R](client: OneForgeClient)(
                implicit
                m1: _task[R]
              ): Algebra[Eff[R, ?]] = new Live[R](client)
}

final class Dummy[R] private[oneforge](
                                        implicit
                                        m1: _task[R]
                                      ) extends Algebra[Eff[R, ?]] {
  override def get(
                    pair: Rate.Pair
                  ): Eff[R, Error Either Rate] =
    for {
      result ← fromTask(Task.now(Rate(pair, Price(BigDecimal(100)), Timestamp.now)))
    } yield Right(result)
}

final class Live[R] private[oneforge](client: OneForgeClient)(
                                        implicit
                                        m1: _task[R]
                                      ) extends Algebra[Eff[R, ?]] {
  override def get(
                    pair: Rate.Pair
                  ): Eff[R, Error Either Rate] =
    for {
      result ← fromTask(client.fetchRate(pair))
    } yield result
}