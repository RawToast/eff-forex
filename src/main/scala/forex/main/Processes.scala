package forex.main

import forex.config._
import forex.services.oneforge.{CurrencyPair, OneForgeClient}
import forex.{processes => p, services => s}
import org.zalando.grafter.macros._

@readerOf[ApplicationConfig]
case class Processes(oneForgeClient: OneForgeClient) {

  final lazy val repository: Map[String, CurrencyPair] = Map.empty

  implicit final lazy val _liveOneForge: s.OneForge[AppEffect] =
    s.OneForge.live[AppStack](oneForgeClient)

  final lazy val Rates = p.Rates[AppEffect]

}
