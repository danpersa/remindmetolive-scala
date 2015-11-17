package com.remindmetolive

import scala.collection.immutable.Map

/**
  * @author dpersa
  */
trait MetaService {

  def getMetas(key: String): Map[String, Any]
}
