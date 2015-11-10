package com.remindmetolive.handler

import com.remindmetolive.{CategoryMetas, PostMetas}
import com.remindmetolive.service.BeardTemplateService
import de.zalando.beard.renderer._
import io.undertow.server.{HttpHandler, HttpServerExchange}
import io.undertow.util.HttpString
import monifu.concurrent.Implicits.globalScheduler
import monifu.reactive.Ack.Continue
import monifu.reactive.{Ack, Observer}

import scala.concurrent.Future
import scala.util.matching.Regex

/**
  * @author dpersa
  */
trait TemplateHandler extends HttpHandler {
  def templateContext(exchange: HttpServerExchange): TemplateContext

  val templateService = BeardTemplateService

  val renderer = templateService.renderer

  override def handleRequest(exchange: HttpServerExchange) = {
    val context = templateContext(exchange)

    val compiledTemplate = templateService.compiledTemplate(context.templateName)

    val renderResult = new MonifuRenderResult()

    val result = renderer.render(compiledTemplate, renderResult, context.model)

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

case class TemplateContext(templateName: String, model: Map[String, Any])

class BeardTemplateHandler(templateContext: TemplateContext) extends TemplateHandler {
  override def templateContext(exchange: HttpServerExchange): TemplateContext = templateContext
}

object PostTemplateHandler extends TemplateHandler {
  private val postPattern = "/(.+)/(.+).html".r

  override def templateContext(exchange: HttpServerExchange): TemplateContext = {
    val postPattern(categoryUrlKey, postUrlKey) = exchange.getRequestURI
    val postMeta = PostMetas.metas(categoryUrlKey)(postUrlKey)


    TemplateContext(templateName = s"/posts/$categoryUrlKey/${postMeta.publishDate}-$postUrlKey",
      model = postMeta.toMap)
  }
}

object CategoryTemplateHandler extends TemplateHandler {
  private val categoryPattern = "/(.+)/".r

  override def templateContext(exchange: HttpServerExchange): TemplateContext = {
    val categoryPattern(categoryUrlKey) = exchange.getRequestURI
    val categoryMeta = CategoryMetas.metas(categoryUrlKey)


    TemplateContext(templateName = s"/categories/$categoryUrlKey",
      model = categoryMeta.toMap)
  }
}

object BeardTemplateHandler {
  def apply(templateName: String, model: Map[String, Any]): BeardTemplateHandler = {
    new BeardTemplateHandler(TemplateContext(templateName, model))
  }
}