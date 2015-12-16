package com.remindmetolive

import java.io.File

import com.typesafe.config.ConfigFactory
import org.slf4j.LoggerFactory

import scala.collection.immutable.{Map, Seq}
import scala.util.Try

/**
  * @author dpersa
  */
object PostMetas
{
  private val logger = LoggerFactory.getLogger(this.getClass)

  private def categoryDirs(category: String): Seq[String] = Assets.assetsSubdir(s"templates/posts/$category")

  def init() = {
    var temp = Map[String, Map[String, PostMeta]]()
    for (category <- Assets.postDirs) {
      for {
        postFileName <- categoryDirs(category)
        if postFileName != "category.conf"
        if postFileName.endsWith(".conf")
      } {
        val config = ConfigFactory.parseFile(new File(s"${Assets.assetsDir}/templates/posts/$category/$postFileName"))
        val key = config.getString("url_key")

        val meta = PostMeta(
          key = key,
          category = category,
          title = config.getString("title"),
          author = config.getString("author"),
          keywords = config.getString("keywords"),
          description = config.getString("description"),
          tags = config.getString("tags"),
          imageName = config.getString("image_name"),
          categoryImageName = Try(config.getString("category_image_name")).getOrElse(config.getString("image_name")),
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
    temp
  }

  val metas = init()

  logger.debug(s"Post Metas: $metas")
}

case class PostMeta(key: String, category: String, title: String, author: String,
                    keywords: String, description: String, tags: String, imageName: String, categoryImageName: String,
                    status: String, publishDate: String, intro: String) {
  val toMap = Map(
    "key" -> key,
    "category" -> category,
    "title" -> title,
    "author" -> author,
    "keywords" -> keywords,
    "description" -> description,
    "tags" -> tags,
    "imageName" -> imageName,
    "categoryImageName" -> categoryImageName,
    "status" -> status,
    "publishDate" -> publishDate,
    "intro" -> intro
  )
}
