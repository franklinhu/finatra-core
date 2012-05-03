package com.capotej.finatra_core

import scala.collection.mutable.HashSet
import scala.collection.mutable.ListBuffer

// Generic controller, receive a generic request, and returns a generic response

case class GenericRequest
  (var path: String, 
   var method: String = "GET",
   var params: Map[String, String] = Map(),
   var headers: Map[String, String] = Map())

case class GenericResponse
  (var body: Array[Byte], 
   var headers: Map[String, String] = Map(),
   var status: Int = 200)


abstract class Controller(var prefix: String = "") {

  var routes: HashSet[(String, PathPattern, Function1[GenericRequest, GenericResponse])] = HashSet()

  def addRoute(method: String, path: String)(callback: Function1[GenericRequest, GenericResponse]) {
    val regex = SinatraPathPatternParser(path)
    routes += Tuple3(method, regex, callback)
  }

  def dispatch(request: GenericRequest): GenericResponse = {
    findRoute(request) match {
      case Some((method, pattern, callback)) => 
        callback(request)
      case None => 
        request.method = "GET"
        findRoute(request) match {
          case Some((method, pattern, callback)) => 
            callback(request)
          case None =>
            new GenericResponse(body = "Not Found".getBytes, status = 404)
        }
    }
    
  //  findRoute(request) match {
  //    case Some((method, pattern, callback)) =>
  //      callback(request)
  //    case None => 
  //      new GenericResponse(body = "Not Found".getBytes, status = 404)
  //  }
  }

  def get(path: String)   (callback: Function1[GenericRequest, GenericResponse]) { addRoute("GET", prefix + path)(callback) }
  def delete(path: String)(callback: Function1[GenericRequest, GenericResponse]) { addRoute("DELETE", prefix + path)(callback) }
  def post(path: String)  (callback: Function1[GenericRequest, GenericResponse]) { addRoute("POST", prefix + path)(callback) }
  def put(path: String)   (callback: Function1[GenericRequest, GenericResponse]) { addRoute("PUT", prefix + path)(callback) }
  def head(path: String)  (callback: Function1[GenericRequest, GenericResponse]) { addRoute("HEAD", prefix + path)(callback) }
  def patch(path: String) (callback: Function1[GenericRequest, GenericResponse]) { addRoute("PATCH", prefix + path)(callback) }

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
 
  // def headers() = { Router.request.getHeaders }
  // def header(name:String) = { Router.request.getHeader(name)}
  // def params(name:String) = { Router.params(name) }
  // def render(path:String) = { Router.renderTemplate(path) }
  // def multiPart(name:String) = { Router.multiParams(name) }
  // def response() = { Router.response }
  // def request() = { Router.request }
  // def headers(pair:Tuple2[String,String]) = { response.headers += pair }
  // def status(code:Int) = { response.status = code }
  // def contentType(mtype:String) = { response.mediaType = mtype }
  def renderBytes(arr: Array[Byte]) = { new GenericResponse(body = arr, status = 200, headers = Map()) }
  def renderString(str: String) = { new GenericResponse(body = str.getBytes, status = 200, headers = Map()) }
  def redirect(url: String) = { new GenericResponse(body = "Moved".getBytes, status = 301, headers = Map("Location" -> url)) }
}
