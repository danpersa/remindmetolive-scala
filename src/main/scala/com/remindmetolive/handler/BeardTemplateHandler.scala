package com.remindmetolive.handler

import java.time.LocalDate
import java.time.format.DateTimeFormatter

import com.remindmetolive.service.BeardTemplateService
import com.remindmetolive._
import de.zalando.beard.renderer._
import io.undertow.server.{HttpHandler, HttpServerExchange}
import io.undertow.util.HttpString
import monifu.concurrent.Implicits.globalScheduler
import monifu.reactive.Ack.Continue
import monifu.reactive.{Ack, Observer}

import scala.concurrent.Future
import scala.collection.immutable.Seq

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
  private val monthFormatter = DateTimeFormatter.ofPattern("MMM")
  private val dayFormatter = DateTimeFormatter.ofPattern("dd")

  private def createModel(postMeta: PostMeta, postPath: String) = {

    val date = LocalDate.parse(postMeta.publishDate, DateTimeFormatter.ISO_LOCAL_DATE)

    // TODO in prod, we don't need /assets
    val imageUrl = s"/assets/images/$postPath/${postMeta.imageName}"

    postMeta.toMap
      .updated("day", dayFormatter.format(date))
      .updated("month", monthFormatter.format(date))
      .updated("tagsList", postMeta.tags.split(",").toSeq.map { tag =>
        Map("name" -> tag)
      }.toList)
      .updated("imageUrl", imageUrl)
  }

  override def templateContext(exchange: HttpServerExchange): TemplateContext = {
    val postPattern(categoryUrlKey, postUrlKey) = exchange.getRequestURI
    val postMeta = PostMetas.metas(categoryUrlKey)(postUrlKey)

    val postPath = s"$categoryUrlKey/${postMeta.publishDate}-$postUrlKey"
    val model = createModel(postMeta, postPath)

    TemplateContext(templateName = s"/posts/$postPath",
      model = model)
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