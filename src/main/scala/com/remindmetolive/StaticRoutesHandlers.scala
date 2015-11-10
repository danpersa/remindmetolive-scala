package com.remindmetolive

import java.io.StringWriter

import com.mitchellbosecke.pebble.PebbleEngine
import com.mitchellbosecke.pebble.loader.ClasspathLoader
import com.remindmetolive.handler.{BeardTemplateHandler, PebbleTemplateHandler, DelegatingHandler}
import de.zalando.beard.renderer.{ClasspathTemplateLoader, CustomizableTemplateCompiler, BeardTemplateRenderer, DefaultTemplateCompiler, MonifuRenderResult, TemplateName}
import io.undertow.server.{HttpHandler, HttpServerExchange}
import io.undertow.util.HttpString
import monifu.concurrent.Implicits.globalScheduler
import monifu.reactive.Ack.Continue
import monifu.reactive.{Ack, Observer}

import scala.collection.parallel.Splitter
import scala.concurrent.Future

/**
 * @author dpersa
 */
object StaticRoutesHandlers {

  val statusHandler = new HttpHandler {
    override def handleRequest(exchange: HttpServerExchange) = {
      exchange.getResponseSender.send("Hello World")
    }
  }

  val helloHandler = new HttpHandler {
    def handleRequest(exchange: HttpServerExchange) = {
      exchange.getResponseHeaders().add(new HttpString("Content-Type"), "text/html")

      exchange.startBlocking()

      exchange.getOutputStream.write("<div>Hello f;dsaljfsda f;dasjkfdsa  fdsa fdsafda fdsaf dsafdsafdsa fdsajkfl ;jadslkf dasf;lk safj;saklf djsa;lkf j;asdlfj as;kfjdsa</div>".getBytes())
      exchange.getOutputStream.flush()
      Thread.sleep(1000)
      exchange.getOutputStream.write("<div>World fdsa fdas fads fdas</div>".getBytes())
      exchange.getOutputStream.flush()
      Thread.sleep(1000)
      exchange.getOutputStream.write("<div>World fdsa fdas fads fdas</div>".getBytes())
      exchange.endExchange()
    }
  }

  val defaultHandler = new HttpHandler {

    val postPattern = "/(.+)/(.+).html".r

    def handleRequest(exchange: HttpServerExchange) = {

      val postPattern(categoryUrlKey, postUrlKey) = exchange.getRequestURI


      val postMeta = PostMetas.metas(categoryUrlKey)(postUrlKey)
      val template = s"/posts/$categoryUrlKey/${postMeta.publishDate}-$postUrlKey"

      //println(s"Post with category: $categoryUrlKey and url key: $postUrlKey and template: $template")

      BeardTemplateHandler(template, postMeta.toMap).handleRequest(exchange)
    }
  }

  def defaultHandler1 = new HttpHandler {
    def handleRequest(exchange: HttpServerExchange) = {
      println(s"XXXX AAA2 FAIL ${exchange.getRequestURI}")
    }
  }

  val templateName = "/home/send-contact"
  val context = Map("title" -> "title XXX",
    "takeLook" -> "Take a Look XXX",
    "stories" -> "Stories.",
    "whoWeAre" -> "Who we are XXX",
    "getInTouch" -> "Get In Touch XXX",
    "berlin" -> "Berlin XXX")

  val indexHandler = DelegatingHandler(BeardTemplateHandler("/home/index", Map.empty))

  val beardHandler = BeardTemplateHandler(templateName, context)



  val pebbleHandler = new PebbleTemplateHandler(templateName, context)

}
