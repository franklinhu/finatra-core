package com.capotej.finatra_core

import org.scalatest.junit.JUnitRunner
import org.scalatest.FlatSpec
import org.scalatest.matchers.ShouldMatchers
import org.junit.runner.RunWith

class FakeApp extends FinatraController {
  get("/") { request => "resp" }

  get("/other") { r => "otherresp" }

  head("/other") { r => "specialresp" }

  get("/name/is/:name") { r => r.params("name") }
}

class OtherApp extends FinatraController {
  get("/hey") { r => "other guy" }
}


@RunWith(classOf[JUnitRunner])
class ControllerCollectionSpec extends FlatSpec with ShouldMatchers {

  val fakeApp = new FakeApp
  val otherApp = new OtherApp
  val controllers = new ControllerCollection

  controllers.add(fakeApp)
  controllers.add(otherApp)

  "GET /" should "respond 200" in {

    val request = new FinatraRequest(path = "/")
    var response = controllers.dispatch(request)

    response should equal (Some("resp"))
  }


  "GET /hey" should "respond 200" in {

    val request = new FinatraRequest(path = "/hey")
    var response = controllers.dispatch(request)

    response should equal (Some("other guy"))
  }

}

@RunWith(classOf[JUnitRunner])
class FinatraControllerSpec extends FlatSpec with ShouldMatchers {

  val fakeApp = new FakeApp

  "GET /" should "respond 200" in {

    val request = new FinatraRequest(path = "/")
    var response = fakeApp.dispatch(request)

    response should equal (Some("resp"))
  }

  "HEAD /" should "respond 200" in {
    val request = new FinatraRequest(path = "/", method = "HEAD")
    var response = fakeApp.dispatch(request)

    response should equal (Some("resp"))
  }

  "HEAD /other" should "respond 200" in {

    val request = new FinatraRequest(path = "/other", method = "HEAD")
    var response = fakeApp.dispatch(request)

    response should equal (Some("specialresp"))
  }

  "GET /other" should "respond 200" in {
    val request = new FinatraRequest(path = "/other")
    var response = fakeApp.dispatch(request)

    response should equal (Some("otherresp"))
  }


  "GET /name/is/bob" should "render bob"  in {
    val request = new FinatraRequest(path = "/name/is/bob")
    var response = fakeApp.dispatch(request)

    response should equal (Some("bob"))
  }

}

