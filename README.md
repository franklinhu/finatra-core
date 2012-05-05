finatra-core
============
## Description
An easy way to embed the popular [Sinatra](http://sinatrarb.com) routing DSL into your Scala framework


## Installation

```xml
<repositories>
  <repository>
    <id>repo.juliocapote.com</id>
    <url>http://repo.juliocapote.com</url>
  </repository>
</repositories>

<dependencies>
  <dependency>
    <groupId>com.capotej</groupId>
    <artifactId>finatra-core</artifactId>
    <version>0.0.5/version>
  </dependency>
</dependencies>
```

## Embedding Guide

#### An example controller definition

```scala
import com.capotej.finatra_core.{Controller => FinatraController, GenericRequest}

class MyApp extends MyFrameworkController with FinatraController {
  get("/hello") { request =>
    Tuple3(200, "hey", Map())
  }
  
  get("/my/name/is/:name") { request => 
    Tuple3(200, request.params("name"), Map()) 
  }
}
```

#### Using the controller
Here we have a ```PretendServer``` that calls ```handleRequest``` with it's own ```SomeKindOfRequest``` (which has ridiculous methods for accessing its fields) object for every incoming request and returns an ```Tuple3``` like ```(200, "hey", Map())```. All we need to do is adapt those to finatra's ```GenericRequest``` and ```GenericResponse``` objects, respectively.

```scala

import com.capotej.finatra_core.{Controller, GenericRequest, GenericResponse}

object PretendServer {
  val myApp = new MyApp

  def handleRequest(rawRequest:SomeKindOfRequest):AnResponse = {
    //Build the Generic Request based on SomeKindOfRequest
    val request = new GenericRequest(path=rawRequest.getThePath,
                       headers=rawRequest.theHeadersGetThem, 
                       method=rawRequest.whatIsTheMethod)
    
    myApp.dispatch(request)
  }
}
```

#### Multiple controllers
If you have multiple controllers, you can use the ```Controllers``` class to encapsulate dispatching over many controllers. Example:

```scala
import com.capotej.finatra_core.{Controller, Controllers, GenericRequest, GenericResponse}


object PretendServer {
  val myApp = new MyApp
  val otherApp = new OtherApp
  val anotherApp = new AnotherApp
  
  val controllers = new Controllers
  
  def init() = {
     controllers.register(myApp)
     controllers.register(otherApp)
     controllers.register(anotherApp)
  }  

  def handleRequest(rawRequest:SomeKindOfRequest):AnResponse = {
    //Build the Generic Request based on SomeKindOfRequest
    val request = new GenericRequest(path=rawRequest.getThePath,
                       headers=rawRequest.theHeadersGetThem, 
                       method=rawRequest.whatIsTheMethod)
    
    val response = controllers.dispatch(request)
  	(response.status, response.body, response.headers)
  }
}

```



 




 


