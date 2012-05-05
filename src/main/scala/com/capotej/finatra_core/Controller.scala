package com.capotej.finatra_core

import scala.collection.mutable.HashSet
import scala.collection.mutable.ListBuffer

// Generic controller, receive a generic request, and returns a generic response

case class GenericRequest
  (var path: String, 
   var method: String = "GET",
   var body: Array[Byte] = Array(),
   var params: Map[String, String] = Map(),
   var headers: Map[String, String] = Map())



class Controllers {
  var ctrls: Seq[Controller] = Seq()

  def dispatch(request: GenericRequest) = {
    var response:Any = null
    ctrls.find { ctrl => 
      ctrl.dispatch(request) match {
        case Some(callbackResponse) => 
          response = callbackResponse 
          true
        case None => 
          false
      }
    } 
    response
  }

  def register(controller: Controller) {
    ctrls = ctrls ++ Seq(controller)  
  }

}

abstract trait Controller {
  var prefix: String = "" 

  var routes: HashSet[(String, PathPattern, Function1[GenericRequest, Option[_]])] = HashSet()

  def addRoute(method: String, path: String)(callback: Function1[GenericRequest, Option[_]]) {
    val regex = SinatraPathPatternParser(path)
    routes += Tuple3(method, regex, callback)
  }

  def dispatch(request: GenericRequest):Option[_] = {
    findRoute(request) match {
      case Some((method, pattern, callback)) => 
        callback(request)
      case None => 
        request.method = "GET"
        findRoute(request) match {
          case Some((method, pattern, callback)) => 
            Some(callback(request))
          case None =>
            None
        }
    }
    
  }

  def get(path: String)   (callback: Function1[GenericRequest, Option[_]]) { addRoute("GET", prefix + path)(callback) }
  def delete(path: String)(callback: Function1[GenericRequest, Option[_]]) { addRoute("DELETE", prefix + path)(callback) }
  def post(path: String)  (callback: Function1[GenericRequest, Option[_]]) { addRoute("POST", prefix + path)(callback) }
  def put(path: String)   (callback: Function1[GenericRequest, Option[_]]) { addRoute("PUT", prefix + path)(callback) }
  def head(path: String)  (callback: Function1[GenericRequest, Option[_]]) { addRoute("HEAD", prefix + path)(callback) }
  def patch(path: String) (callback: Function1[GenericRequest, Option[_]]) { addRoute("PATCH", prefix + path)(callback) }

  def extractParams(request:GenericRequest, xs: Tuple2[_, _]) = {
    request.params += Tuple2(xs._1.toString, xs._2.asInstanceOf[ListBuffer[String]].head.toString)
  }
  
  def findRoute(request: GenericRequest) = {
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
