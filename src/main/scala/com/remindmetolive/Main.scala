package com.remindmetolive

import java.io.StringReader
import java.nio.file.Paths

import io.undertow.Handlers._
import io.undertow.predicate.Predicates
import io.undertow.server.handlers.{PredicateHandler, PathTemplateHandler}
import io.undertow.server.handlers.builder.PredicatedHandler
import io.undertow.server.handlers.resource.PathResourceManager
import io.undertow.server.{HttpHandler, HttpServerExchange}
import io.undertow.{Handlers, Undertow}
import org.slf4j.LoggerFactory

/**
 * @author dpersa
 */
object Main extends App {

  val logger = LoggerFactory.getLogger(this.getClass)

  PostMetas
  CategoryMetas

  val pathHandler = Handlers.path().addExactPath("/status", new HttpHandler {
    override def handleRequest(exchange: HttpServerExchange) = exchange.dispatch(StaticRoutesHandlers.helloHandler)
  }).addExactPath("/index", new HttpHandler {
    override def handleRequest(exchange: HttpServerExchange) = exchange.dispatch(StaticRoutesHandlers.indexHandler)
  }).addExactPath("/beard", new HttpHandler {
    override def handleRequest(exchange: HttpServerExchange) = exchange.dispatch(StaticRoutesHandlers.beardHandler)
  }).addPrefixPath("/assets", resource(new PathResourceManager(Paths.get("/Users/dpersa/prog/scala/remindmetolive-scala/target/assets"), 100)).setDirectoryListingEnabled(true))
    .addPrefixPath("/aaa", new PathTemplateHandler() {
  }.add("{category}/{post}", new HttpHandler {
    override def handleRequest(exchange: HttpServerExchange) = exchange.dispatch(StaticRoutesHandlers.defaultHandler)
  })).addPrefixPath("/", new PredicateHandler(Predicates.and(Predicates.suffix(".html"), Predicates.parse("path-template[value=\"/{category}/{username}\"]")), new HttpHandler {
    override def handleRequest(exchange: HttpServerExchange) = exchange.dispatch(StaticRoutesHandlers.defaultHandler)
  }, new HttpHandler {
    override def handleRequest(exchange: HttpServerExchange) = exchange.dispatch(StaticRoutesHandlers.defaultHandler1)
  }))
  //    .addPrefixPath("/", new HttpHandler {
  //    override def handleRequest(exchange: HttpServerExchange) = {
  //      logger.debug("Dispatch to the routing handler")
  //      //exchange.dispatch(routingHandler)
  //    }
  //  })

  val server = Undertow.builder
    .addHttpListener(8080, "0.0.0.0")
    .setIoThreads(400)
    .setWorkerThreads(400)
    .setHandler(pathHandler)
    .build
  server.start

}
