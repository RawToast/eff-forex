package forex.config

import org.zalando.grafter.macros._

import scala.concurrent.duration.FiniteDuration

@readers
case class ApplicationConfig(
                              akka: AkkaConfig,
                              api: ApiConfig,
                              executors: ExecutorsConfig,
                              oneforge: OneforgeConfig
)

case class AkkaConfig(
    name: String,
    exitJvmTimeout: Option[FiniteDuration]
)

case class ApiConfig(
    interface: String,
    port: Int
)

case class ExecutorsConfig(
    default: String
)

case class OneforgeConfig(
                           apikey: String,
                           apiversion: String,
                           baseurl: String
)
