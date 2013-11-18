package com.ingi

import akka.actor.{ActorSystem, Props}
import akka.io.IO
import spray.can.Http
import util.Properties
import akka.event.Logging
import com.typesafe.config.ConfigFactory

object Boot extends App {

  // we need an ActorSystem to host our application in
  implicit val system = ActorSystem("on-spray-can", ConfigFactory.load.getConfig("akka"))
  
  val log = Logging.getLogger(system, this)
  log.info("Hi!")

  // create and start our service actor
  val service = system.actorOf(Props[MyServiceActor], "back-end-service")

  val port = Properties.envOrElse("PORT", "8080").toInt // for Heroku compatibility

  // start a new HTTP server on port 8080 with our service actor as the handler
  IO(Http) ! Http.Bind(service, "0.0.0.0", port)
}