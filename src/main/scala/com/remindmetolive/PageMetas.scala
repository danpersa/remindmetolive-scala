package com.remindmetolive

import java.util.concurrent.Callable
import java.io.File
import com.google.common.cache.{Cache, CacheBuilder}
import com.typesafe.config.ConfigFactory
import org.slf4j.LoggerFactory

import scala.collection.immutable.Map

/**
  * @author dpersa
  */
case class PageMetas(val configDir: String) extends MetaService {
  private val logger = LoggerFactory.getLogger(this.getClass)

  private val cache: Cache[String, Map[String, Any]] = CacheBuilder.newBuilder().build()

  override def getMetas(key: String): Map[String, Any] = {
    cache.get(key, new Callable[Map[String, Any]] {
      override def call(): Map[String, Any] = {
        val config = ConfigFactory.parseFile(new File(s"${Assets.assetsDir}/$configDir/$key.conf"))
        PageMeta(
          key = key,
          title = config.getString("title"),
          keywords = config.getString("keywords"),
          description = config.getString("description")
        ).toMap
      }
    })
  }
}

case class PageMeta(key: String, title: String, keywords: String, description: String) {
  def toMap: Map[String, Any] = {
    Map(
      "key" -> key,
      "title" -> title,
      "keywords" -> keywords,
      "description" -> description
    )
  }
}
