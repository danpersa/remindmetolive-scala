package com.remindmetolive.handler

import com.remindmetolive._
import com.remindmetolive.service.BeardTemplateService
import de.zalando.beard.renderer._
import io.undertow.server.{HttpHandler, HttpServerExchange}
import io.undertow.util.HttpString
import monifu.concurrent.Implicits.globalScheduler
import monifu.reactive.Ack.Continue
import monifu.reactive.{Ack, Observer}

import scala.concurrent.Future

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
    val postMeta = PostMetas.byCategoryAndUrlKey(categoryUrlKey, postUrlKey)
    val postPath = s"$categoryUrlKey/${postMeta("publishDate")}-$postUrlKey"

    TemplateContext(templateName = s"/posts/$postPath",
      model = postMeta)
  }
}

case class IndexTemplateHandler(val pageMetas: PageMetas) extends PageKeyTemplateHandler {
  override def pageKey(exchange: HttpServerExchange): String = "index"
}

trait PageKeyTemplateHandler extends TemplateHandler {

  def pageMetas: PageMetas

  def pageKey(exchange: HttpServerExchange): String

  override def templateContext(exchange: HttpServerExchange): TemplateContext = {

    val key = pageKey(exchange)

    val metas = pageMetas.getMetas(key)

    TemplateContext(templateName = s"/home/$key",
      model = metas)
  }
}

case class PageTemplateHandler(val pageMetas: PageMetas) extends PageKeyTemplateHandler {
  private val homePagePattern = "/(.+)/".r

  override def pageKey(exchange: HttpServerExchange): String = {
    val homePagePattern(pageKey) = exchange.getRequestURI
    pageKey
  }
}

object CategoryTemplateHandler extends TemplateHandler {
  private val categoryPattern = "/(.+)/".r

  private def createModel(categoryUrlKey: String) = {
    val categoryMeta = CategoryMetas.metas(categoryUrlKey)
    val postMetas = PostMetas.publishedForCategory(categoryUrlKey)

    categoryMeta.toMap
      .updated("postMetas", postMetas)
  }

  override def templateContext(exchange: HttpServerExchange): TemplateContext = {
    val categoryPattern(categoryUrlKey) = exchange.getRequestURI

    val model = createModel(categoryUrlKey)

    TemplateContext(templateName = s"/categories/$categoryUrlKey",
      model = model)
  }
}

object BeardTemplateHandler {
  def apply(templateName: String, model: Map[String, Any]): BeardTemplateHandler = {
    new BeardTemplateHandler(TemplateContext(templateName, model))
  }
}