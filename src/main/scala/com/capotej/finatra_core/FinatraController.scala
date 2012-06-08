package com.capotej.finatra_core

import scala.collection.mutable.HashSet
import scala.collection.mutable.ListBuffer

// Generic controller, receive a generic request, and returns a generic response

case class FinatraRequest(
  var path: String,
  var method: String = "GET",
  var body: Array[Byte] = Array(),
  var params: Map[String, String] = Map(),
  var multiParams: Map[String, MultipartItem] = Map(),
  var headers: Map[String, String] = Map(),
  var cookies: Map[String, FinatraCookie] = Map()
)

case class FinatraCookie(
  var expires: Int,
  var value: String,
  var comment: String,
  var commentUrl: String,
  var domain: String,
  var path: String,
  var version: Int,
  var name: String,
  var ports: Set[Int],
  var isDiscard: Boolean,
  var isHttpOnly: Boolean,
  var isSecure: Boolean
)

class ControllerCollection {
  var ctrls: Seq[FinatraController] = Seq()

  def dispatch(request: FinatraRequest):Option[Any] = {
    var response:Option[Any] = None
    ctrls.find { ctrl =>
      ctrl.dispatch(request) match {
        case Some(callbackResponse) =>
          response = Some(callbackResponse)
          true
        case None =>
          false
      }
    }
    response
  }

  def add(controller: FinatraController) {
    ctrls = ctrls ++ Seq(controller)
  }

}

abstract trait FinatraController {
  var prefix: String = ""

  var routes: HashSet[(String, PathPattern, Function1[FinatraRequest, Any])] = HashSet()

  def addRoute(method: String, path: String)(callback: Function1[FinatraRequest, Any]) {
    val regex = SinatraPathPatternParser(path)
    routes += Tuple3(method, regex, callback)
  }

  def dispatch(request: FinatraRequest):Option[Any] = {
    request.multiParams = MultipartParsing.loadMultiParams(request)
    findRoute(request) match {
      case Some((method, pattern, callback)) =>
        Some(callback(request))
      case None =>
        if (request.method == "HEAD") {
          request.method = "GET"
        }
        findRoute(request) match {
          case Some((method, pattern, callback)) =>
            Some(callback(request))
          case None =>
            None
        }
    }

  }

  def get(path: String)   (callback: Function1[FinatraRequest, Any]) { addRoute("GET", prefix + path)(callback) }
  def delete(path: String)(callback: Function1[FinatraRequest, Any]) { addRoute("DELETE", prefix + path)(callback) }
  def post(path: String)  (callback: Function1[FinatraRequest, Any]) { addRoute("POST", prefix + path)(callback) }
  def put(path: String)   (callback: Function1[FinatraRequest, Any]) { addRoute("PUT", prefix + path)(callback) }
  def head(path: String)  (callback: Function1[FinatraRequest, Any]) { addRoute("HEAD", prefix + path)(callback) }
  def patch(path: String) (callback: Function1[FinatraRequest, Any]) { addRoute("PATCH", prefix + path)(callback) }

  def extractParams(request:FinatraRequest, xs: Tuple2[_, _]) = {
    request.params += Tuple2(xs._1.toString, xs._2.asInstanceOf[ListBuffer[String]].head.toString)
  }

  def findRoute(request: FinatraRequest) = {
    var thematch:Option[Map[_,_]] = None
    routes.find( route => route match {
      case (_method, pattern, callback) =>
        thematch = pattern(request.path)
        if(thematch.getOrElse(null) != null && _method == request.method) {
          thematch.getOrElse(null).foreach(xs => extractParams(request, xs))
          true
        } else {
          false
        }
    })
  }

}
