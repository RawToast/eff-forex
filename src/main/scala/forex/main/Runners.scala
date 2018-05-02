package forex.main

import com.typesafe.scalalogging.Logger
import forex.config._
import monix.eval.Task
import org.atnos.eff._
import syntax.all._
import org.atnos.eff.syntax.addon.monix.task._
import org.zalando.grafter.macros._

@readerOf[ApplicationConfig]
case class Runners() {

  val logger = Logger("forex")

  def runApp[R](
      app: AppEffect[R]
  ): Task[R] = {
    app.runWriterUnsafe[String](logger.info(_))
      .runAsync
  }
}
