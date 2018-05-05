package forex.processes.rates

import forex.domain._
import scala.util.control.NoStackTrace

package messages {
  sealed trait ErrorMessage extends Throwable with NoStackTrace
  object ErrorMessage {
    final case class Generic(msg: String) extends ErrorMessage
    final case class NotFound(msg: String) extends ErrorMessage
    final case class System(underlying: Throwable) extends ErrorMessage
  }

  final case class GetRequest(
      from: Currency,
      to: Currency
  )
}
