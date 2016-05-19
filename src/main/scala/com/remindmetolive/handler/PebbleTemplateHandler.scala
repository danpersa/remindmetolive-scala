package com.remindmetolive.handler

import java.io.StringWriter

import com.mitchellbosecke.pebble.PebbleEngine
import com.mitchellbosecke.pebble.loader.ClasspathLoader
import io.undertow.server.{HttpHandler, HttpServerExchange}
import io.undertow.util.HttpString

/**
  * @author dpersa
  */
case class PebbleTemplateHandler(val templateName: String, context: Map[String, AnyRef]) extends HttpHandler {

  val loader = new ClasspathLoader()
  loader.setSuffix(".beard.html")
  loader.setPrefix("pebble")

  val engine = new PebbleEngine.Builder()
    .loader(loader)
    .build()

  val template = engine.getTemplate(templateName)

  override def handleRequest(exchange: HttpServerExchange) = {

    val sr = new StringWriter()

    template.evaluate(sr)


    exchange.getResponseHeaders().add(new HttpString("Content-Type"), "text/html")
    exchange.startBlocking()
    exchange.getOutputStream.write(sr.toString().getBytes())
  }
}

