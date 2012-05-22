package com.capotej.finatra_core

import com.capotej.finatra_test._
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner

class MyApp extends FinatraController {
  get("/path") { request => "get:path" }
  post("/path") { request => "post:path" }
  put("/path") { request => "put:path" }
  delete("/path") { request => "delete:path" }
  patch("/path") { request => "patch:path" }
  get("/params") { request => request.params("p") }
  get("/headers") { request => request.headers("Referer") }
}

@RunWith(classOf[JUnitRunner])
class FinatraHelperSpec extends FinatraSpec {

  app = new MyApp

  "GET /path" should "respond 200" in {
    get("/path")
    lastResponse should equal (Some("get:path"))
  }

  "POST /path" should "respond 200" in {
    post("/path")
    lastResponse should equal (Some("post:path"))
  }

  "PUT /path" should "respond 200" in {
    put("/path")
    lastResponse should equal (Some("put:path"))
  }

  "DELETE /path" should "respond 200" in {
    delete("/path")
    lastResponse should equal (Some("delete:path"))
  }

  "PATCH /path" should "respond 200" in {
    patch("/path")
    lastResponse should equal (Some("patch:path"))
  }

  "GET /params" should "respond 200" in {
    get("/params", Map("p"->"yup"))
    lastResponse should equal (Some("yup"))
  }

  "GET /headers" should "respond 200" in {
    get("/headers", headers=Map("Referer"->"http://twitter.com"))
    lastResponse should equal (Some("http://twitter.com"))
  }

}