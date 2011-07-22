package com.excilys.ebi.gatling.statistics.presenter

import com.excilys.ebi.gatling.core.log.Logging

import com.excilys.ebi.gatling.statistics.extractor.ActiveSessionsDataExtractor
import com.excilys.ebi.gatling.statistics.template.ActiveSessionsTemplate
import com.excilys.ebi.gatling.statistics.template.Series
import com.excilys.ebi.gatling.statistics.writer.TemplateWriter

class ActiveSessionsDataPresenter extends DataPresenter with Logging {
  def generateGraphFor(runOn: String, menuItems: Map[String, String]) = {

    val seriesList = List(new Series("Active Sessions", new ActiveSessionsDataExtractor(runOn).getResults.map { e => (getDateForHighcharts(e._1), e._2) }))

    val output = new ActiveSessionsTemplate(runOn, menuItems, seriesList).getOutput

    new TemplateWriter(runOn, "active_sessions.html").writeToFile(output)
  }
}
