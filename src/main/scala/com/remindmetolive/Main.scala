package com.remindmetolive

import java.nio.file.Paths

import com.remindmetolive.handler._
import io.undertow.Handlers._
import io.undertow.predicate.Predicates
import io.undertow.server.handlers.{BlockingHandler, PredicateHandler}
import io.undertow.server.handlers.resource.PathResourceManager
import io.undertow.{Handlers, Undertow}
import org.slf4j.LoggerFactory

/**
  * @author dpersa
  */
object Main extends App {

  val logger = LoggerFactory.getLogger(this.getClass)

  PostMetas
  CategoryMetas
  val homePageMetas = PageMetas("templates/home")
  val homePageTemplateHandler = PageTemplateHandler(homePageMetas)


  val pathHandler = Handlers.path().addExactPath("/status", StaticRoutesHandlers.statusHandler)
    .addExactPath("/", new BlockingHandler(IndexTemplateHandler(homePageMetas)))
    .addExactPath("/about", new BlockingHandler(homePageTemplateHandler))
    .addExactPath("/contact", new BlockingHandler(homePageTemplateHandler))
    .addExactPath("/stories", new BlockingHandler(CategoryTemplateHandler))
    .addExactPath("/streets-of-berlin", new BlockingHandler(CategoryTemplateHandler))
    .addExactPath("/cats", new BlockingHandler(CategoryTemplateHandler))
    .addExactPath("/beard", new BlockingHandler(StaticRoutesHandlers.beardHandler))
    .addExactPath("/pebble", new BlockingHandler(StaticRoutesHandlers.pebbleHandler))
    .addPrefixPath("/assets", resource(new PathResourceManager(Paths.get("/Users/dpersa/prog/scala/remindmetolive-scala/target/assets"), 100)).setDirectoryListingEnabled(true))
    .addPrefixPath("/", new PredicateHandler(Predicates.and(Predicates.suffix(".html"),
      Predicates.parse("path-template(value=\"/{category}/{post}\")")),
      new BlockingHandler(PostTemplateHandler)
      , new BlockingHandler(StaticRoutesHandlers.defaultHandler1)))

  val server = Undertow.builder
    .addHttpListener(8080, "0.0.0.0")
    .setIoThreads(400)
    .setWorkerThreads(400)
    .setHandler(pathHandler)
    .build
  server.start

}
