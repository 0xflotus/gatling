/**
 * Copyright 2011 eBusiness Information, Groupe Excilys (www.excilys.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.excilys.ebi.gatling.statistics.generator

import scala.io.Source
import com.excilys.ebi.gatling.core.config.GatlingConfig._
import com.excilys.ebi.gatling.core.util.PathHelper._
import com.excilys.ebi.gatling.core.result.writer.FileDataWriter._
import com.excilys.ebi.gatling.statistics.generator.builtin._
import scala.tools.nsc.io.File
import com.excilys.ebi.gatling.core.log.Logging
import scala.tools.nsc.io.Directory

class ChartsGenerator extends Logging {

	def generateFor(runOn: String) = {

		val jQueryFile = File(GATLING_ASSETS_JQUERY)
		val highstocksFile = File(GATLING_ASSETS_HIGHSTOCK)
		val highchartsFile = File(GATLING_ASSETS_HIGHCHARTS)

		val jsAssetsPath = GATLING_RESULTS_FOLDER + "/" + runOn + GATLING_JS
		val jQueryDestPath = jsAssetsPath + GATLING_JQUERY
		val highstocksDestPath = jsAssetsPath + GATLING_HIGHSTOCK
		val highchartsDestPath = jsAssetsPath + GATLING_HIGHCHARTS

		File(jsAssetsPath).toDirectory.createDirectory()
		jQueryFile.copyTo(jQueryDestPath, true)
		highstocksFile.copyTo(highstocksDestPath, true)
		highchartsFile.copyTo(highchartsDestPath, true)

		val generator = new CompositeChartGenerator(new ActiveSessionsChartGenerator, new GlobalRequestsChartGenerator, new DetailsRequestsChartGenerator, new MenuItemsChartGenerator)

		for (line <- Source.fromFile(GATLING_RESULTS_FOLDER + "/" + runOn + "/" + GATLING_SIMULATION_LOG_FILE, CONFIG_GATLING_ENCODING).getLines) {
			line.split("\t") match {
				// If we have a well formated result
				case Array(runOn, scenarioName, userId, actionName, executionStartDate, executionDuration, resultStatus, resultMessage, groups) =>
					val groupsArray = groups.stripPrefix(GROUPS_PREFIX).stripSuffix(GROUPS_SUFFIX).split(GROUPS_SEPARATOR)
					generator.onRow(runOn, scenarioName, userId, actionName, executionStartDate, executionDuration, resultStatus, resultMessage, groupsArray.toList)
				// Else, if the resulting data is not well formated print an error message
				case _ => logger.warn("simulation.log had bad end of file, statistics will be generated but may not be accurate")
			}
		}

		generator.generateChartFor(runOn)
	}
}