/*
 * Copyright (c) 2015, BoDao, Inc. All Rights Reserved.
 *
 * Author@ dgl
 *
 * This software is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
 * CONDITIONS OF ANY KIND, either express or implied.
 *
 *
 * you can send a http request and get the http response Conveniently by this software.
 *
 * can use http's post and get way to create a request.
 *
 */

package common.HttpHelper

import java.io.{BufferedReader, InputStream, InputStreamReader}
import java.net.{HttpURLConnection, URL}

import common.LogHelper.LogHelper

import scala.collection.mutable.Map
import scala.io.Source


object HttpHelper {

  private def setPostConnection(conn: HttpURLConnection): Unit = {
    conn.setConnectTimeout(20 * 1000);
    conn.setRequestMethod("POST");
    conn.setDoInput(true);
    conn.setDoOutput(true);
    conn.setUseCaches(false);
  }

  private def setGetConnection(conn: HttpURLConnection): Unit = {
    conn.setConnectTimeout(10 * 1000);
    conn.setRequestMethod("GET");
    conn.setDoInput(true);
    conn.setDoOutput(true);
    conn.setUseCaches(false);
  }

  /* we can put some keys in http header */
  private def setConnectionHeader(conn: HttpURLConnection, headerInfo: Map[String, String]): Unit = {
    if (headerInfo != null) {
      headerInfo.keys.foreach { item =>
        conn.setRequestProperty(item, headerInfo(item));
      }
    }
  }

  /* make a string by InputStream */
  def getStrFromInputSteam(in: InputStream): String =  {
    val bfr = new BufferedReader(new InputStreamReader(in, "UTF-8"));
    val strbuf = new StringBuffer();
    var line = "";

    line = bfr.readLine();
    while(line != null){
      strbuf.append(line + "\n");
      line = bfr.readLine();
    }

    return strbuf.toString();
  }

  /* post data to data server */
  def postData(data: String, dataserver: String, headerInfo: Map[String, String]): Boolean = {
    try {
      val entity = data.getBytes("UTF-8");
      val url = new URL(dataserver);
      val conn = url.openConnection.asInstanceOf[HttpURLConnection];
      conn.setRequestProperty("Content-Length", String.valueOf(entity.length));
      setPostConnection(conn);
      setConnectionHeader(conn, headerInfo);

      val output = conn.getOutputStream();
      output.write(entity);

      val code = conn.getResponseCode();
      output.close();
      conn.disconnect();

      if (code == 200) return true
      else return false;
    } catch {
      case ex: Exception =>
        LogHelper.err(s"connect to ${dataserver} have exception. ${ex.getMessage()}");
        return false;
    }
  }

  /* send a request to server, return the status code and page in a map */
  def httpGetRequest(urlStr: String, headerInfo: Map[String, String]): Map[String, String] =  {
    val result = Map[String, String]();
    try {
      val url = new URL(urlStr);
      val conn = url.openConnection.asInstanceOf[HttpURLConnection];
      setGetConnection(conn);
      setConnectionHeader(conn, headerInfo);

      var inStream = conn.getInputStream();
      val page = getStrFromInputSteam(inStream);
      val code = conn.getResponseCode().toString;

      result += ("code" -> code);
      result += ("page" -> page);
      if (conn != null) conn.disconnect();
    } catch {
      case ex: Exception =>
        LogHelper.err(s"connect to ${urlStr} have exception. ${ex.getMessage()}");
    }

    return result;
  }

  /* send a request to server, return the as a string */
  def getWebPage(host: String, path: String): String =  {
    try {
      val url = host + path;
      val pageRet = Source.fromURL(url, "utf-8").mkString;
      return pageRet;
    } catch {
      case ex: Exception =>
        LogHelper.err(s"connect to ${host}${path} have exception. ${ex.getMessage()}");
        return Unit.toString;
    }
  }

  def getWebPage(url: String): String = {
    try {
      val pageRet = Source.fromURL(url, "utf-8").mkString;
      return pageRet;
    } catch {
      case ex: Exception =>
        LogHelper.err(s"connect to ${url} have exception. ${ex.getMessage()}");
        return Unit.toString;
    }
  }
}

