package forex.processes.rates

import forex.domain._
import scala.util.control.NoStackTrace

package messages {
  sealed trait Error extends Throwable with NoStackTrace
  object Error {
    final case class Generic(msg: String) extends Error
    final case class NotFound(msg: String) extends Error
    final case class System(underlying: Throwable) extends Error
  }

  final case class GetRequest(
      from: Currency,
      to: Currency
  )
}
