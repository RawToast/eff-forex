package forex

import cats.data.Writer
import org.atnos.eff.|=

package object services {

  type OneForge[F[_]] = oneforge.Algebra[F]
  final val OneForge = oneforge.Interpreters
  type OneForgeError = oneforge.Error
  final val OneForgeError = oneforge.Error

  type _writer[R] = Writer[String, ?] |= R
}
