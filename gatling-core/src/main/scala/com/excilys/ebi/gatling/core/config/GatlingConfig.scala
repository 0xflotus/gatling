/*
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
package com.excilys.ebi.gatling.core.config

import com.excilys.ebi.gatling.core.log.Logging
import com.excilys.ebi.gatling.core.util.PathHelper._
import com.excilys.ebi.gatling.core.util.PropertiesHelper._
import com.excilys.ebi.gatling.core.util.StringHelper._
import scala.io.Codec

/**
 * Configuration loader of Gatling
 */
object GatlingConfig extends Logging {

	/**
	 * Contains the configuration of Gatling
	 */
	val config: GatlingConfiguration =
		try {
			// Locate configuration file, depending on users options
			val configFile =
				if (GATLING_CONFIG_PROPERTY != EMPTY) {
					logger.info("Loading custom configuration file: conf/{}", GATLING_CONFIG_PROPERTY)
					GATLING_CONFIG_FOLDER + "/" + GATLING_CONFIG_PROPERTY
				} else {
					logger.info("Loading default configuration file")
					GATLING_CONFIG_FOLDER + "/" + GATLING_CONFIG_FILE
				}

			GatlingConfiguration.fromFile(configFile)
		} catch {
			case e =>
				logger.error("{}\n{}", e.getMessage, e.getStackTraceString)
				throw new Exception("Could not parse configuration file.")
		}

	/**
	 * Gatling global encoding value
	 */
	val CONFIG_ENCODING = config.getString("gatling.encoding", Codec.UTF8.name)
	/**
	 * Gatling simulation timeout value
	 */
	val CONFIG_SIMULATION_TIMEOUT = config.getInt("gatling.simulation.timeout", 86400)

	val CONFIG_CHARTING_COMPONENT_LIBRARY_CLASS = config.getString("gatling.charting.componentLibraryClass", "com.excilys.ebi.gatling.highcharts.component.HighchartsComponentLibrary")

	val CONFIG_SIMULATION_SCALA_PACKAGE = config.getString("gatling.simulation.scalaPackage", EMPTY)

	val CONFIG_CHARTING_INDICATORS_LOWER_BOUND = config.getInt("gatling.charting.indicators.lowerBound", 100)

	val CONFIG_CHARTING_INDICATORS_HIGHER_BOUND = config.getInt("gatling.charting.indicators.higherBound", 500)
}
