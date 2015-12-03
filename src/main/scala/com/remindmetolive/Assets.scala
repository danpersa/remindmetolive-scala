package com.remindmetolive

import java.io.File

import com.typesafe.config.ConfigFactory

import scala.collection.immutable.Seq

/**
  * @author dpersa
  */
object Assets {

  private val config = ConfigFactory.load()
  val assetsDir = config.getString("assets.dir")
  val postDirs: Seq[String] = Assets.assetsSubdir("templates/posts")


  def assetsSubdir(subdir: String) = {

    val dir = new File(s"${Assets.assetsDir}/$subdir")
    if (!dir.exists()) {
      throw new IllegalStateException(s"Assets subdir ${subdir} not found")
    }
    dir.list().toList
  }
}
