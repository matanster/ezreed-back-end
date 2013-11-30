package com.ingi

import akka.actor.Actor
import spray.routing._
import spray.http._
import MediaTypes._
import akka.event.Logging
import spray.util.LoggingContext
import scala.io.Source

// we don't implement our route structure directly in the service actor because
// we want to be able to test it independently, without having to spin up an actor
class MyServiceActor extends Actor with MyService {

  // the HttpService trait defines only one abstract member, which
  // connects the services environment to the enclosing actor or test
  def actorRefFactory = context

  //val log = Logging(context.system, this) //(LoggingContext, this)
  //log.info("foo")
 
  // this actor only runs our route, but you could add
  // other things here, like request stream processing
  // or timeout handling
  def receive = runRoute(myRoute)
}

// this trait defines our service behavior independently from the service actor
trait MyService extends HttpService {
  
  //akka.event.Logging.Debug

  val myRoute =
    path("none") {
      get {
        logRequestResponse("Request & response logging for root path", Logging.InfoLevel) {
	        respondWithMediaType(`text/html`) { // XML is marshalled to `text/xml` by default, so we simply override here
	          complete {
	            <html>
              <body>
                <h1>This is the Spray.io based back-end server</h1>
              </body>
            </html>
	          }
	        }
        }
      }
    } ~
    pathPrefix("serve-original-as-html" / RestPath) { subPath =>
      get {
        logRequestResponse("Request & response logging for serve-original-as-html", Logging.InfoLevel) {
            getFromFile("../local-copies/html-converted/" + subPath)
        }
      }
    } ~
    path("parameter-extraction-example-code") {
      get {
        logRequestResponse("Request & response logging for serve-original-as-html", Logging.InfoLevel) {
            parameters('filename) { (filename) =>
                getFromFile("../local-copies/html-converted/" + filename)
            	//complete(s"$location") // just send back the requested file location 
        	}
        }
      }
    } ~
    pathPrefix("process-file" / RestPath) { subPath =>
      get {
        complete(processFile("../local-copies/html-converted/" + subPath))
      }
    } ~ 
    get { 
      // this is only for logging requests for unhandled paths
      // before spray returns the 404
      logRequestResponse("Request & response logging for undefined resource request", Logging.InfoLevel) {
     	reject
      }
    }
      
    
    def processFile(location: String /* ctx: RequestContext */): String = {
      val html = Source.fromFile(location).mkString
      //val xhtml = scala.xml.Xhtml.toXhtml(html) 
      //scala.xml.Xhtml.
      "finished processing"
    } 
}