finatra-core
============
## Description
An easy way to embed the popular [Sinatra](http://sinatrarb.com) routing DSL into your Scala web framework


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

#### Our "pretend" framework
For this example, let's suppose we have a ```PretendFramework```, that handles incoming requests by calling a ```handleRequest``` method on classes that extend a ```PretendController``` parent class and it handles responses by returning a ```Tuple3``` like ```(200, "helo", Map())```. It also gives you methods like ```init``` and ```start```.

For instance:

```scala

class MyApp extends PretendController {
  
  def handleRequest(request: PretendRequest) = {
  	if(request.getThePath == "/hello")
  	  Tuple3(200, "world", Map())
  	} elsif(request.getThePath == "/foo") {
   	  Tuple3(200, "bar", Map())
  	} else { 
  	  Tuple3(404, "not found", Map())
  	}
  }
}

//Starting the app
val myApp = new MyApp

myApp.start()
```

Now, let's adapt that into the Sinatra style DSL.


#### Hooking into the framework

```scala
import com.capotej.finatra_core._

class MyAdaptedController extends PretendController with FinatraController {
  def handleRequest(request: PretendRequest) = {
    
    val newRequest = new FinatraRequest(path=rawRequest.getThePath,
                       headers=rawRequest.theHeadersGetThem, 
                       method=rawRequest.whatIsTheMethod)
    
    dispatch(newRequest) match {
      case Some(resp) =>
        //remember, this is an Any, so you need to cast it to whatever your framework needs 
        resp.asInstanceOf[Tuple3[Int, String, Map[String, String]]]
      case None =>
        Tuple3(404, "not found", Map())
    } 
  }
}

class MyApp extends AdaptedFinatraController {
  get("/hello") { request =>
    Tuple3(200, "hey", Map())
  }
  
  get("/foo") { request =>
    Tuple3(200, "bar", Map())
  
  get("/my/name/is/:name") { request => 
    Tuple3(200, request.params("name"), Map()) 
  }
}

//Starting the app
val myApp = new myApp

myApp.start()
```

#### Multiple controllers
If you have multiple controllers, you can use the ```ControllerCollection``` class to encapsulate dispatching over many controllers. Example: (cont'd from above)

```scala
class AnotherApp extends AdaptedFinatraController {
  get("/bar") { request =>
    Tuple3(200, "baz", Map())
  }
}

class MainApp extends PretendController {
  val controllers = new ControllerCollection 
  
  def init {
    val myApp = new myApp
    val anotherApp = new anotherApp

    controllers.add(myApp)
    controllers.add(anotherApp)
  }

  def handleRequest(request: PretendRequest) = {
    controllers.dispatch(request) match {
      case Some(resp) =>
        //remember, this is an Any, so you need to cast it to whatever your framework needs 
        resp.asInstanceOf[Tuple3[Int,String,Map[String,String]]]
      case None =>
        Tuple3(404, "not found", Map())
    }
  }
}

//Starting the app
val mainApp = new MainApp

mainApp.start()

```

### Notes/Gotchas

The ```dispatch``` method for either the ```FinatraController``` trait or ```ControllerCollection``` returns an ```Option[Any]```, where ```Some``` is when a route was found for that request, and ```None``` is when its not. This means you'll have to implement your own error handling.




 




 


