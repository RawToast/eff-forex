package forex.main

import com.typesafe.scalalogging.Logger
import forex.config._
import forex.domain.Rate
import monix.eval.Task
import org.atnos.eff.syntax.addon.monix.task._
import org.atnos.eff.syntax.all._
import org.zalando.grafter.macros._

@readerOf[ApplicationConfig]
case class Runners() {

  val logger = Logger("forex")

  var state: List[Rate] = List.empty[Rate]

  def runApp[R](
                 app: AppEffect[R]
               ): Task[R] = {

    app
    .runWriterUnsafe[String](logger.info(_))
    .runState[List[Rate]](state)
    .runAsync
    .map { case (result, ns: List[Rate]) =>
      state = ns
      result
    }
  }
}
