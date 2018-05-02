package forex.interfaces.api.utils

import akka.http.scaladsl._
import akka.http.scaladsl.model.HttpResponse
import forex.processes._
import forex.processes.rates.messages.Error
import akka.http.scaladsl.model.StatusCodes.InternalServerError

object ApiExceptionHandler {

  def apply(): server.ExceptionHandler =
    server.ExceptionHandler {
      case re: RatesError ⇒
        ctx ⇒re match {
          case Error.Generic(msg) => ctx.complete(
            HttpResponse(InternalServerError, entity = s"Something went wrong in the rates process: $msg" ))
          case Error.System(_) => ctx.complete(
            HttpResponse(InternalServerError, entity = "Something went wrong in the rates process"))
        }
      case _: Throwable ⇒
        ctx ⇒
          ctx.complete(
            HttpResponse(InternalServerError, entity = "Something else went wrong"))
    }

}
