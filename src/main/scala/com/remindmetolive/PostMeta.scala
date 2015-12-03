package com.remindmetolive

import java.io.File
import java.net.URL

import com.google.common.cache.{Cache, CacheBuilder}
import com.typesafe.config.ConfigFactory
import org.slf4j.LoggerFactory

import scala.io.Source
import scala.collection.immutable.Map
import scala.collection.immutable.Seq

/**
  * @author dpersa
  */
object PostMetas
//  extends MetaService
{
  private val logger = LoggerFactory.getLogger(this.getClass)

  private var temp = Map[String, Map[String, PostMeta]]()

  //private val cache: Cache[String, Map[String, Any]] = new CacheBuilder[String, Map[String, Any]]().build()

  private def categoryDirs(category: String): Seq[String] = Assets.assetsSubdir(s"templates/posts/$category")

  def init() {
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

  //override def getMetas(key: String): Map[String, Any] = ???
}

case class PostMeta(key: String, category: String, title: String, author: String,
                    keywords: String, description: String, tags: String, pictureUrl: String,
                    status: String, publishDate: String, intro: String) {
  val toMap = Map(
    "key" -> key,
    "category" -> category,
    "title" -> title,
    "author" -> author,
    "keywords" -> keywords,
    "description" -> description,
    "tags" -> tags,
    "pictureUrl" -> pictureUrl,
    "status" -> status,
    "publishDate" -> publishDate,
    "intro" -> intro
  )
}
