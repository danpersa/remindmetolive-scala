package com.remindmetolive.service

import java.util.concurrent.Callable

import com.google.common.cache.{CacheBuilder, Cache}
import com.remindmetolive.Assets
import de.zalando.beard.ast.BeardTemplate
import de.zalando.beard.renderer.{TemplateName, BeardTemplateRenderer, FileTemplateLoader, CustomizableTemplateCompiler}

/**
  * @author dpersa
  */
object BeardTemplateService {

  private val templateLoader = new FileTemplateLoader(s"${Assets.assetsDir}/templates", ".beard.html")
  private val compiler = new CustomizableTemplateCompiler(templateLoader = templateLoader)

  private val cache: Cache[String, BeardTemplate] = CacheBuilder
    .newBuilder()
    .initialCapacity(100)
    .concurrencyLevel(10)
    .build()

  val renderer = new BeardTemplateRenderer(compiler)

  def compiledTemplate(templateName: String) = cache.get(templateName, new Callable[BeardTemplate] {
    override def call(): BeardTemplate = {
      compiler.compile(TemplateName(templateName)).get
    }
  })
}
