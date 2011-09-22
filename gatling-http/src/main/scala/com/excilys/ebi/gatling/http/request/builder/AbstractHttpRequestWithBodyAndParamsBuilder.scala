package com.excilys.ebi.gatling.http.request.builder

import com.ning.http.client.Request
import com.ning.http.client.RequestBuilder
import com.ning.http.client.FluentStringsMap

import com.excilys.ebi.gatling.core.context.Context
import com.excilys.ebi.gatling.core.context.FromContext

import com.excilys.ebi.gatling.http.request.Param
import com.excilys.ebi.gatling.http.request.StringParam
import com.excilys.ebi.gatling.http.request.ContextParam
import com.excilys.ebi.gatling.http.request.HttpRequestBody

abstract class AbstractHttpRequestWithBodyAndParamsBuilder[B <: AbstractHttpRequestWithBodyAndParamsBuilder[B]](urlFormatter: Option[Context => String], queryParams: Option[Map[String, Param]], params: Option[Map[String, Param]],
                                                                                                                headers: Option[Map[String, String]], body: Option[HttpRequestBody], followsRedirects: Option[Boolean])
    extends AbstractHttpRequestWithBodyBuilder[B](urlFormatter, queryParams, headers, body, followsRedirects) {

  override def build(context: Context): Request = {
    requestBuilder setMethod getMethod
    addParamsTo(requestBuilder, params, context)
    super.build(context)
  }

  def newInstance(urlFormatter: Option[Context => String], queryParams: Option[Map[String, Param]], params: Option[Map[String, Param]], headers: Option[Map[String, String]], body: Option[HttpRequestBody], followsRedirects: Option[Boolean]): B

  def newInstance(urlFormatter: Option[Context => String], queryParams: Option[Map[String, Param]], headers: Option[Map[String, String]], body: Option[HttpRequestBody], followsRedirects: Option[Boolean]): B = {
    newInstance(urlFormatter, queryParams, params, headers, body, followsRedirects)
  }

  def withParam(paramKey: String, paramValue: String): B = {
    newInstance(urlFormatter, queryParams, Some(params.get + (paramKey -> StringParam(paramValue))), headers, body, followsRedirects)
  }

  def withParam(paramKey: String, paramValue: FromContext): B = {
    newInstance(urlFormatter, queryParams, Some(params.get + (paramKey -> ContextParam(paramValue.attributeKey))), headers, body, followsRedirects)
  }

  def withParam(paramKey: String): B = withParam(paramKey, FromContext(paramKey))

  private def addParamsTo(requestBuilder: RequestBuilder, params: Option[Map[String, Param]], context: Context) = {
    requestBuilder setParameters new FluentStringsMap
    for (param <- params.get) {
      param._2 match {
        case StringParam(string) => requestBuilder addParameter (param._1, string)
        case ContextParam(string) => requestBuilder addParameter (param._1, context.getAttribute(string))
      }
    }
  }
}