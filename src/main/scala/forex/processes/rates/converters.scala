package forex.processes.rates

import forex.services._

package object converters {
  import messages._

  def toProcessError[T <: Throwable](t: T): Error = t match {
    case OneForgeError.Generic(msg) ⇒ Error.Generic(msg)
    case OneForgeError.NotFound(msg) ⇒ Error.NotFound(msg)
    case OneForgeError.System(err) ⇒ Error. System(err)
    case e: Error                  ⇒ e
    case e                         ⇒ Error.System(e)
  }

}
