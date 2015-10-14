package com.remindmetolive

import java.io.StringWriter

import com.mitchellbosecke.pebble.PebbleEngine
import com.mitchellbosecke.pebble.loader.ClasspathLoader
import de.zalando.beard.renderer.{ClasspathTemplateLoader, CustomizableTemplateCompiler, BeardTemplateRenderer, DefaultTemplateCompiler, MonifuRenderResult, TemplateName}
import io.undertow.server.{HttpHandler, HttpServerExchange}
import io.undertow.util.HttpString
import monifu.concurrent.Implicits.globalScheduler
import monifu.reactive.Ack.Continue
import monifu.reactive.{Ack, Observer}

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

  def defaultHandler = new HttpHandler {
    def handleRequest(exchange: HttpServerExchange) = {
      println("XXXX AAA1 MATCH")
    }
  }


  def defaultHandler1 = new HttpHandler {
    def handleRequest(exchange: HttpServerExchange) = {
      println("XXXX AAA2 FAIL")
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

  val beardHandler = new BeardTemplateHandler(templateName, context)

  case class BeardTemplateHandler(val templateName: String, context: Map[String, Any]) extends HttpHandler {

    val compiler = new CustomizableTemplateCompiler(templateLoader = new ClasspathTemplateLoader("/templates", ".beard.html"))
    val renderer = new BeardTemplateRenderer(compiler)
    val compiledTemplate = compiler.compile(TemplateName(templateName)).get

    override def handleRequest(exchange: HttpServerExchange) = {

      val renderResult = new MonifuRenderResult()

      val result = renderer.render(compiledTemplate, renderResult, context)

      exchange.getResponseHeaders().add(new HttpString("Content-Type"), "text/html")
      exchange.startBlocking()
      exchange.dispatch()

      result.subscribe(new Observer[String] {
        override def onError(ex: Throwable): Unit = ???

        override def onComplete(): Unit = exchange.endExchange()

        override def onNext(elem: String): Future[Ack] = {
          exchange.getOutputStream.write(elem.getBytes)
          Future(Continue)
        }
      })
    }
  }

  val pebbleHandler = new PebbleTemplateHandler(templateName, context)

  case class PebbleTemplateHandler(val templateName: String, context: Map[String, AnyRef]) extends HttpHandler {

    val loader = new ClasspathLoader()
    loader.setSuffix(".beard.html")
    loader.setPrefix("pebble")

    val engine = new PebbleEngine(loader)
    val template = engine.getTemplate(templateName)

    override def handleRequest(exchange: HttpServerExchange) = {

      val sr = new StringWriter()

      template.evaluate(sr)


      exchange.getResponseHeaders().add(new HttpString("Content-Type"), "text/html")
      exchange.startBlocking()
      exchange.getOutputStream.write(sr.toString().getBytes())
    }
  }
}
