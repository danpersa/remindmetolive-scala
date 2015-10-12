package com.remindmetolive

import io.undertow.server.{HttpServerExchange, HttpHandler}

/**
 * @author dpersa
 */
class DelegatingHandler private(val httpHandler: HttpHandler) extends HttpHandler {

  override def handleRequest(exchange: HttpServerExchange): Unit =
    exchange.dispatch(httpHandler)
}

object DelegatingHandler {
  def apply(httpHandler: HttpHandler): HttpHandler = {
    new DelegatingHandler(httpHandler)
  }
}