package com.capotej.finatra_test

import com.capotej.finatra_core._
import org.scalatest.FlatSpec
import org.scalatest.matchers.ShouldMatchers

class FinatraSpec extends FlatSpec with ShouldMatchers{

  var lastResponse:Any  = null
  var app:FinatraController = null

  def get(path:String, params:Map[String,String]=Map(), headers:Map[String,String]=Map()) {
    buildRequest("GET",path,params,headers)
  }

  def post(path:String, params:Map[String,String]=Map(), headers:Map[String,String]=Map()) {
    buildRequest("POST",path,params,headers)
  }

  def put(path:String, params:Map[String,String]=Map(), headers:Map[String,String]=Map()) {
    buildRequest("PUT",path,params,headers)
  }

  def delete(path:String, params:Map[String,String]=Map(), headers:Map[String,String]=Map()) {
    buildRequest("DELETE",path,params,headers)
  }

  def head(path:String,params:Map[String,String]=Map(), headers:Map[String,String]=Map()) {
    buildRequest("HEAD",path,params,headers)
  }

  def patch(path:String, params:Map[String,String]=Map(), headers:Map[String,String]=Map()) {
    buildRequest("PATCH",path,params,headers)
  }

  def buildRequest(method:String, path:String, params:Map[String,String]=Map(), headers:Map[String,String]=Map()) {
    val request   = new FinatraRequest(method=method,path=path,params=params,headers=headers)
    lastResponse  = app.dispatch(request)
  }
}