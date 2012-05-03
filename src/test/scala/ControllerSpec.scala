package com.capotej.finatra_core

import org.scalatest.junit.JUnitRunner
import org.scalatest.FlatSpec
import org.scalatest.matchers.ShouldMatchers
import org.junit.runner.RunWith

class FakeApp extends Controller {
  get("/") { request =>
    renderString("resp")
  }

  get("/other") { r => renderString("otherresp") }
  
  head("/other") { r => renderString("specialresp") }

  get("/redirectme") { r => redirect("/gohere") }

  get("/name/is/:name") { r => renderString(r.params("name")) }
}

class OtherApp extends Controller {
  get("/hey") { r => renderString("other guy")}
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

    response.status should equal (200)
    response.body should equal ("resp".getBytes)
  }

  
  "GET /hey" should "respond 200" in {

    val request = new GenericRequest(path = "/hey")
    var response = controllers.dispatch(request)

    response.status should equal (200)
    response.body should equal ("other guy".getBytes)
  }

}

@RunWith(classOf[JUnitRunner])
class ControllerSpec extends FlatSpec with ShouldMatchers {

  val fakeApp = new FakeApp

  "GET /" should "respond 200" in {

    val request = new GenericRequest(path = "/")
    var response = fakeApp.dispatch(request)

    response.status should equal (200)
    response.body should equal ("resp".getBytes)
  }

  "HEAD /" should "respond 200" in {
    val request = new GenericRequest(path = "/", method = "HEAD")
    var response = fakeApp.dispatch(request)

    response.status should equal (200)
    response.body should equal ("resp".getBytes)
  }

  "HEAD /other" should "respond 200" in {

    val request = new GenericRequest(path = "/other", method = "HEAD")
    var response = fakeApp.dispatch(request)

    response.status should equal (200)
    new String(response.body) should equal ("specialresp")
  }

  "GET /other" should "respond 200" in {
    val request = new GenericRequest(path = "/other")
    var response = fakeApp.dispatch(request)

    response.status should equal (200)
    response.body should equal ("otherresp".getBytes)
  }

  "GET /redirect" should "redirect"  in {
    val request = new GenericRequest(path = "/redirectme")
    var response = fakeApp.dispatch(request)

    response.status should equal (301)
    response.headers.get("Location") should equal (Some("/gohere"))
  }
  
  "GET /name/is/bob" should "render bob"  in {
    val request = new GenericRequest(path = "/name/is/bob")
    var response = fakeApp.dispatch(request)

    response.status should equal (200)
    response.body should equal ("bob".getBytes)
  }

}

