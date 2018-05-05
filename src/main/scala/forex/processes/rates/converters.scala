package forex.processes.rates

import forex.services._

package object converters {
  import messages._

  def toProcessError[T <: Throwable](t: T): ErrorMessage = t match {
    case OneForgeError.Generic(msg) ⇒ ErrorMessage.Generic(msg)
    case OneForgeError.NotFound(msg) ⇒ ErrorMessage.NotFound(msg)
    case OneForgeError.System(err) ⇒ ErrorMessage.System(err)
    case e: ErrorMessage                  ⇒ e
    case e                         ⇒ ErrorMessage.System(e)
  }

}
