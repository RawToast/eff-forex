package forex.interfaces.api.utils

import akka.http.scaladsl._
import akka.http.scaladsl.model.HttpResponse
import forex.processes._
import forex.processes.rates.messages.ErrorMessage
import akka.http.scaladsl.model.StatusCodes.{InternalServerError, NotFound}

object ApiExceptionHandler {

  def apply(): server.ExceptionHandler =
    server.ExceptionHandler {
      case re: RatesError ⇒
        ctx ⇒re match {
          case ErrorMessage.NotFound(msg) => ctx.complete(
            HttpResponse(NotFound, entity = msg ))
          case ErrorMessage.Generic(msg) => ctx.complete(
            HttpResponse(InternalServerError, entity = s"Something went wrong in the rates process: $msg" ))
          case ErrorMessage.System(_) => ctx.complete(
            HttpResponse(InternalServerError, entity = "Something went wrong in the rates process"))
        }
      case thr: Throwable ⇒
        ctx ⇒
          ctx.complete(
            HttpResponse(InternalServerError, entity = "Something else went wrong"))
    }

}
