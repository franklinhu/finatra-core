package com.capotej.finatra_core

import org.scalatest.junit.JUnitRunner
import org.scalatest.FlatSpec
import org.scalatest.matchers.ShouldMatchers
import org.junit.runner.RunWith

class FakeApp extends Controller {
  get("/") { request => "resp" }

  get("/other") { r => "otherresp" }
  
  head("/other") { r => "specialresp" }

  get("/name/is/:name") { r => r.params("name") }
}

class OtherApp extends Controller {
  get("/hey") { r => "other guy" }
}


@RunWith(classOf[JUnitRunner])
class ControllersSpec extends FlatSpec with ShouldMatchers {

  val fakeApp = new FakeApp
  val otherApp = new OtherApp
  val controllers = new Controllers

  controllers.register(fakeApp)
  controllers.register(otherApp)

  "GET /" should "respond 200" in {

    val request = new GenericRequest(path = "/")
    var response = controllers.dispatch(request)

    response should equal (Some("resp"))
  }

  
  "GET /hey" should "respond 200" in {

    val request = new GenericRequest(path = "/hey")
    var response = controllers.dispatch(request)
    
    response should equal (Some("other guy"))
  }

}

@RunWith(classOf[JUnitRunner])
class ControllerSpec extends FlatSpec with ShouldMatchers {

  val fakeApp = new FakeApp

  "GET /" should "respond 200" in {

    val request = new GenericRequest(path = "/")
    var response = fakeApp.dispatch(request)

    response should equal (Some("resp"))
  }

  "HEAD /" should "respond 200" in {
    val request = new GenericRequest(path = "/", method = "HEAD")
    var response = fakeApp.dispatch(request)

    response should equal (Some("resp"))
  }

  "HEAD /other" should "respond 200" in {

    val request = new GenericRequest(path = "/other", method = "HEAD")
    var response = fakeApp.dispatch(request)

    response should equal (Some("specialresp"))
  }

  "GET /other" should "respond 200" in {
    val request = new GenericRequest(path = "/other")
    var response = fakeApp.dispatch(request)

    response should equal (Some("otherresp"))
  }

  
  "GET /name/is/bob" should "render bob"  in {
    val request = new GenericRequest(path = "/name/is/bob")
    var response = fakeApp.dispatch(request)

    response should equal (Some("bob"))
  }

}

