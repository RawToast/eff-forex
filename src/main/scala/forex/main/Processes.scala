package forex.main

import forex.config._
import forex.services.oneforge.OneForgeClient
import forex.{processes => p, services => s}
import org.zalando.grafter.macros._

@readerOf[ApplicationConfig]
case class Processes() {

//  implicit final lazy val _dummyOneForge: s.OneForge[AppEffect] =
//    s.OneForge.dummy[AppStack]

  final lazy val client = new OneForgeClient

  implicit final lazy val _liveOneForge: s.OneForge[AppEffect] =
    s.OneForge.live[AppStack](client)

  final val Rates = p.Rates[AppEffect]

}
