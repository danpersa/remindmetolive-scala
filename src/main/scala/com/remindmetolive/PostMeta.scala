package com.remindmetolive

import com.typesafe.config.ConfigFactory
import org.slf4j.LoggerFactory

import scala.io.Source
import scala.collection.immutable.Map

/**
 * @author dpersa
 */
object PostMetas {
  val logger = LoggerFactory.getLogger(this.getClass)

  var temp = Map[String, Map[String, PostMeta]]()

  def init() {
    for (category <- Source.fromInputStream(this.getClass.getResourceAsStream("/templates/posts")).getLines()) {
      for {
        postFileName <- Source.fromInputStream(this.getClass.getResourceAsStream(s"/templates/posts/$category")).getLines()
        if postFileName != "category.conf"
        if postFileName.endsWith(".conf")
      } {
        val config = ConfigFactory.load(s"templates/posts/$category/$postFileName")
        val key = config.getString("url_key")

        val meta = PostMeta(
          key = key,
          category = category,
          title = config.getString("title"),
          author = config.getString("author"),
          keywords = config.getString("keywords"),
          description = config.getString("description"),
          tags = config.getString("tags"),
          pictureUrl = config.getString("picture_url"),
          status = config.getString("status"),
          publishDate = config.getString("publish_date"),
          intro = config.getString("intro")
        )

        temp = temp.get(category) match {
          case Some(map) => temp + (category -> (map + (key -> meta)))
          case None => temp + (category -> Map(key -> meta))
        }

        logger.debug(s"Discovered post ${meta}")
      }
    }
  }

  init()

  val metas = temp

  logger.debug(s"Post Metas: $metas")
}

case class PostMeta(key: String, category: String, title: String, author: String,
                    keywords: String, description: String, tags: String, pictureUrl: String,
                    status: String, publishDate: String, intro: String)
