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
    <artifactId>finatra_core</artifactId>
    <version>0.0.1-SNAPSHOT</version>
  </dependency>
</dependencies>
```

## Embedding Guide

#### An example controller definition

```scala
import com.capotej.finatra_core.Controller

class MyApp extends Controller {
  get("/hello") { request =>
    renderString("world")
  }
}
```

#### Using the controller
Here we have a ```PretendServer``` that calls ```handleRequest``` with it's own ```SomeKindOfRequest``` (which has ridiculous methods for accessing its fields) object for every incoming request and returns an ```Array``` like ```[200, "hey", Map()]```. All we need to do is adapt those to finatra's ```GenericRequest``` and ```GenericResponse``` objects, respectively.

```scala
object PretendServer {
  val myApp = new MyApp

  def handleRequest(rawRequest:SomeKindOfRequest):AnResponse = {
    //Build the Generic Request based on SomeKindOfRequest
    val request = new GenericRequest(path=rawRequest.getThePath,
                       headers=rawRequest.theHeadersGetThem, 
                       method=rawRequest.whatIsTheMethod)
    
    val response = myApp.dispatch(request)
  	(response.status, response.body, response.headers)
  }
}
```





 


