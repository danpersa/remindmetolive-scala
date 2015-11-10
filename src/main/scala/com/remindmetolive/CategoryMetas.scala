package com.remindmetolive

import com.typesafe.config.ConfigFactory
import org.slf4j.LoggerFactory

import scala.io.Source
import scala.collection.immutable.Map

/**
  * @author dpersa
  */
object CategoryMetas {
  val logger = LoggerFactory.getLogger(this.getClass)

  var temp = Map[String, CategoryMeta]()

  def init() {
    for (category <- Source.fromInputStream(this.getClass.getResourceAsStream("/templates/posts")).getLines()) {
      val config = ConfigFactory.load(s"templates/posts/$category/category.conf")

      val meta = CategoryMeta(
        key = category,
        title = config.getString("title"),
        keywords = config.getString("keywords"),
        description = config.getString("description")
      )

      temp = temp + (category -> meta)

      logger.debug(s"Discovered category ${meta}")
    }
  }

  init()

  val metas = temp

  logger.debug(s"Category Metas: $metas")
}

case class CategoryMeta(key: String, title: String, keywords: String, description: String) {
  def toMap = {
    Map(
      "key" -> key,
      "title" -> title,
      "keywords" -> keywords,
      "description" -> description
    )
  }
}
