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

import scala.tools.nsc.io.Path.string2path
import GatlingConfig._

object GatlingFiles {
	/* Global paths */
	private val gatlingHomeEnv = System.getenv("GATLING_HOME")
	val GATLING_HOME = if (gatlingHomeEnv == null) "/tmp" else gatlingHomeEnv // To prevent null pointer exceptions if GATLING_HOME not set
	val GATLING_ASSETS_FOLDER = GATLING_HOME / "assets"
	val GATLING_USER_FILES_FOLDER = GATLING_HOME / "user-files"

	lazy val GATLING_DATA_FOLDER = CONFIG_DATA_FOLDER.getOrElse(GATLING_USER_FILES_FOLDER / "data")
	lazy val GATLING_RESULTS_FOLDER = CONFIG_RESULTS_FOLDER.getOrElse(GATLING_HOME / "results")
	lazy val GATLING_REQUEST_BODIES_FOLDER = CONFIG_REQUEST_BODIES_FOLDER.getOrElse(GATLING_USER_FILES_FOLDER / "request-bodies")
	lazy val GATLING_SCENARIOS_FOLDER = GATLING_USER_FILES_FOLDER / "scenarios"

	/* Assets Paths */
	val GATLING_JS = "js"
	val GATLING_STYLE = "style"
	val GATLING_ASSETS_JS_FOLDER = GATLING_ASSETS_FOLDER / GATLING_JS
	val GATLING_ASSETS_STYLE_FOLDER = GATLING_ASSETS_FOLDER / GATLING_STYLE

	/* Default files and internal constants */
	val GATLING_DEFAULT_CONFIG_FILE = "gatling.conf"
	val GATLING_IMPORTS_FILE = "imports.txt"

	/* Results Paths */
	def resultFolder(runOn: String) = GATLING_RESULTS_FOLDER / runOn
	def jsFolder(runOn: String) = resultFolder(runOn) / GATLING_JS
	def styleFolder(runOn: String) = resultFolder(runOn) / GATLING_STYLE
	def rawdataFolder(runOn: String) = resultFolder(runOn) / "rawdata"
	def simulationLogFile(runOn: String) = resultFolder(runOn) / "simulation.log"
}