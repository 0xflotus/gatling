package com.excilys.ebi.gatling.statistics.presenter

import com.excilys.ebi.gatling.core.util.FileHelper._
import com.excilys.ebi.gatling.statistics.extractor.DetailsRequestsDataExtractor
import com.excilys.ebi.gatling.statistics.result.DetailsRequestsDataResult
import com.excilys.ebi.gatling.statistics.template.DetailsRequestsTemplate
import com.excilys.ebi.gatling.statistics.template.TimeSeries
import com.excilys.ebi.gatling.statistics.template.ColumnSeries
import com.excilys.ebi.gatling.statistics.writer.TemplateWriter
import com.excilys.ebi.gatling.statistics.writer.TSVFileWriter
import com.excilys.ebi.gatling.statistics.utils.HighChartsHelper._
import scala.collection.immutable.TreeMap
import org.apache.commons.lang3.StringUtils

class DetailsRequestsDataPresenter extends DataPresenter[Map[String, DetailsRequestsDataResult]] {

	def generateGraphFor(runOn: String, results: Map[String, DetailsRequestsDataResult]) = {
		results.foreach {
			case (requestName, result) =>

				new TSVFileWriter(runOn, formatToFilename(requestName) + TSV_EXTENSION).writeToFile(result.timeValues.map { e => List(e._1, e._2.toString) })

				val series = List(new TimeSeries(requestName.substring(8), result.timeValues.map { e => (printHighChartsDate(e._1), e._2) }),
					new TimeSeries("medium", result.timeValues.map { e => (printHighChartsDate(e._1), result.medium) }))

				val columnData = new ColumnSeries(requestName.substring(8), result.columnData._1, result.columnData._2)

				val output = new DetailsRequestsTemplate(runOn, series, columnData, requestName, result).getOutput

				new TemplateWriter(runOn, formatToFilename(requestName) + HTML_EXTENSION).writeToFile(output)
		}
	}
}