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

  val pathHandler = Handlers.path().addExactPath("/status", DelegatingHandler(StaticRoutesHandlers.helloHandler))
    .addExactPath("/beard", DelegatingHandler(StaticRoutesHandlers.beardHandler))
    .addPrefixPath("/assets", resource(new PathResourceManager(Paths.get("/Users/dpersa/prog/scala/remindmetolive-scala/target/assets"), 100)).setDirectoryListingEnabled(true))
    .addPrefixPath("/", new PredicateHandler(Predicates.and(Predicates.suffix(".html"), Predicates.parse("path-template[value=\"/{category}/{username}\"]")),
    DelegatingHandler(StaticRoutesHandlers.defaultHandler)
    , DelegatingHandler(StaticRoutesHandlers.defaultHandler1)))

  val server = Undertow.builder
    .addHttpListener(8080, "0.0.0.0")
    .setIoThreads(400)
    .setWorkerThreads(400)
    .setHandler(pathHandler)
    .build
  server.start

}
