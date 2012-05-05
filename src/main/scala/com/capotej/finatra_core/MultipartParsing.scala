package com.capotej.finatra_core

import org.apache.commons.fileupload._
import java.io._

// HERE BE DRAGONS

class MultipartItem(val fileobj:Tuple2[java.util.Map[String,String], ByteArrayOutputStream]) {
  def headers() = {
    this.fileobj._1
  }

  def data() = {
    this.fileobj._2
  }

  def name() = {
    headers.get("name")
  }

  def contentType = {
    headers.get("Content-Type")
  }

  def filename = {
    headers.get("filename")
  }

  def writeToFile(path: String) = {
    val fileout = new FileOutputStream(path)
    data.writeTo(fileout)
    fileout.close
  }
}

object MultipartParsing {

  def loadMultiParams(request: FinatraRequest) = {

    var multiParams = Map[String, MultipartItem]()
    val ctype = request.headers.get("Content-Type").getOrElse(null)
    if(ctype != null){
      val boundaryIndex = ctype.indexOf("boundary=");
      val boundary = ctype.substring(boundaryIndex + 9).getBytes
      val input = new ByteArrayInputStream(request.body)
      try {
        val multistream = new MultipartStream(input, boundary)
        var nextPart = multistream.skipPreamble
        while(nextPart){
          val paramParser = new ParameterParser
          val headers = paramParser.parse(multistream.readHeaders.toString, ';').asInstanceOf[java.util.Map[String,String]]
          val out = new ByteArrayOutputStream
          val name = headers.get("name").toString
          multistream.readBodyData(out)
          val fileobj = new MultipartItem(Tuple2(headers, out))
          multiParams = multiParams + Tuple2(name, fileobj)
          nextPart = multistream.readBoundary
        }
      } catch {
          case e: MultipartStream.MalformedStreamException => println("wrong")
          case e: IOException => println("error")
      }
    }
    multiParams
  }


}
