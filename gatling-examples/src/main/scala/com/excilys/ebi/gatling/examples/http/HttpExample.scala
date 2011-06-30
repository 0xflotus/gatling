package com.excilys.ebi.gatling.examples.http

import com.excilys.ebi.gatling.core.action.Action
import com.excilys.ebi.gatling.core.action.builder.AbstractActionBuilder

import com.excilys.ebi.gatling.http.scenario.HttpScenarioBuilder.{ scenario, chain, HttpScenarioBuilder }
import com.excilys.ebi.gatling.http.runner.HttpRunner.play
import com.excilys.ebi.gatling.http.processor.capture.builder.HttpRegExpCaptureBuilder.regexp
import com.excilys.ebi.gatling.http.processor.capture.builder.HttpXPathCaptureBuilder.xpath

import com.ning.http.client.RequestBuilder
import com.ning.http.client.Request

object HttpExample {
  def run = {
    val iterations = 10
    val concurrentUsers = 5
    val pause1 = 3
    val pause2 = 2
    val pause3 = 1

    val url = "http://localhost/index.html"
    val request: Request = new RequestBuilder setUrl url build

    val lambdaUser =
      scenario("Standard User")
        .doHttpRequest("Page d'accueil", request)
        .pause(pause1)
        .iterate(
          iterations,
          chain.doHttpRequest("Catégorie Poney",
            request,
            xpath("//input[@value='aaaa']/@id") inAttribute "inputbis" build)
            .pause(pause2)
            .doHttpRequest("Liste Articles", request)
            .pause(pause3))
          .doHttpRequest(
            "Ajout au panier",
            request,
            regexp("""<input id="text1" type="text" value="(.*)" />""") inAttribute "input" build)
            .pause(pause3)

    play(lambdaUser, concurrentUsers)
  }
}