package com.excilys.ebi.gatling.http.request.builder

import com.excilys.ebi.gatling.core.log.Logging
import com.excilys.ebi.gatling.core.context.Context
import com.excilys.ebi.gatling.core.context.FromContext
import com.excilys.ebi.gatling.core.feeder.Feeder

import com.excilys.ebi.gatling.http.request.HttpRequestBody
import com.excilys.ebi.gatling.http.request.FilePathBody
import com.excilys.ebi.gatling.http.request.StringBody
import com.excilys.ebi.gatling.http.request.TemplateBody
import com.excilys.ebi.gatling.http.request.Param
import com.excilys.ebi.gatling.http.request.StringParam
import com.excilys.ebi.gatling.http.request.ContextParam
import com.excilys.ebi.gatling.http.request.builder.mixin.RequestParamsAndBody

import com.ning.http.client.RequestBuilder
import com.ning.http.client.Request

import java.io.File

import org.fusesource.scalate._

object PostHttpRequestBuilder {
  class PostHttpRequestBuilder(url: Option[String], queryParams: Option[Map[String, Param]], params: Option[Map[String, Param]],
                               headers: Option[Map[String, String]], body: Option[HttpRequestBody], feeder: Option[Feeder], followsRedirects: Option[Boolean])
      extends HttpRequestBuilder(url, queryParams, params, headers, body, feeder, followsRedirects) with RequestParamsAndBody with Logging {

    def withQueryParam(paramKey: String, paramValue: String) = new PostHttpRequestBuilder(url, Some(queryParams.get + (paramKey -> StringParam(paramValue))), params, headers, body, feeder, followsRedirects)

    def withQueryParam(paramKey: String, paramValue: FromContext) = new PostHttpRequestBuilder(url, Some(queryParams.get + (paramKey -> ContextParam(paramValue.attributeKey))), params, headers, body, feeder, followsRedirects)

    def withQueryParam(paramKey: String) = withQueryParam(paramKey, FromContext(paramKey))

    def withParam(paramKey: String, paramValue: String) = new PostHttpRequestBuilder(url, queryParams, Some(params.get + (paramKey -> StringParam(paramValue))), headers, body, feeder, followsRedirects)

    def withParam(paramKey: String, paramValue: FromContext) = new PostHttpRequestBuilder(url, queryParams, Some(params.get + (paramKey -> ContextParam(paramValue.attributeKey))), headers, body, feeder, followsRedirects)

    def withParam(paramKey: String) = withQueryParam(paramKey, FromContext(paramKey))

    def withHeader(header: Tuple2[String, String]) = new PostHttpRequestBuilder(url, queryParams, params, Some(headers.get + (header._1 -> header._2)), body, feeder, followsRedirects)

    def asJSON = new PostHttpRequestBuilder(url, queryParams, params, Some(headers.get + ("Accept" -> "application/json") + ("Content-Type" -> "application/json")), body, feeder, followsRedirects)

    def asXML = new PostHttpRequestBuilder(url, queryParams, params, Some(headers.get + ("Accept" -> "application/xml") + ("Content-Type" -> "application/xml")), body, feeder, followsRedirects)

    def withFile(filePath: String) = new PostHttpRequestBuilder(url, queryParams, params, headers, Some(FilePathBody(filePath)), feeder, followsRedirects)

    def withBody(body: String) = new PostHttpRequestBuilder(url, queryParams, params, headers, Some(StringBody(body)), feeder, followsRedirects)

    def withTemplateBodyFromContext(tplPath: String, values: Map[String, String]) = new PostHttpRequestBuilder(url, queryParams, params, headers, Some(TemplateBody(tplPath, values.map { value => (value._1, ContextParam(value._2)) })), feeder, followsRedirects)

    def withTemplateBody(tplPath: String, values: Map[String, String]) = new PostHttpRequestBuilder(url, queryParams, params, headers, Some(TemplateBody(tplPath, values.map { value => (value._1, StringParam(value._2)) })), feeder, followsRedirects)

    def withFeeder(feeder: Feeder) = new PostHttpRequestBuilder(url, queryParams, params, headers, body, Some(feeder), followsRedirects)

    def followsRedirect(followRedirect: Boolean) = new PostHttpRequestBuilder(url, queryParams, params, headers, body, feeder, Some(followRedirect))

    def build(context: Context): Request = build(context, "POST")
  }

  def post(url: String) = new PostHttpRequestBuilder(Some(url), Some(Map()), Some(Map()), Some(Map()), None, None, None)
}