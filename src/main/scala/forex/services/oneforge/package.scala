package forex.services

import cats.data.{State, Writer}
import forex.domain.Rate
import org.atnos.eff.|=

package object oneforge {
  type _writer[R] = Writer[String, ?] |= R
  type _state[R] = State[List[Rate], ?] |= R
}
