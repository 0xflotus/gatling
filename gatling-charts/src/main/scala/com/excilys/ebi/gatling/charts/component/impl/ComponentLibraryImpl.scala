/**
 * Copyright 2011-2012 eBusiness Information, Groupe Excilys (www.excilys.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * 		http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.excilys.ebi.gatling.charts.component.impl

import org.joda.time.DateTime

import com.excilys.ebi.gatling.charts.component.{ ComponentLibrary, Component }
import com.excilys.ebi.gatling.charts.series.Series

class ComponentLibraryImpl extends ComponentLibrary {

	def getActiveSessionsChartComponent(series: Series[DateTime, Int]*): Component = throw new UnsupportedOperationException
	def getRequestsChartComponent(allRequests: Series[DateTime, Int], failedRequests: Series[DateTime, Int], succeededRequests: Series[DateTime, Int], pieSeries: Series[String, Int], allActiveSessions: Series[DateTime, Int]): Component = throw new UnsupportedOperationException
	def getTransactionsChartComponent(allTransactions: Series[DateTime, Int], failedTransactions: Series[DateTime, Int], succeededTransactions: Series[DateTime, Int], pieSeries: Series[String, Int], allActiveSessions: Series[DateTime, Int]): Component = throw new UnsupportedOperationException
	def getRequestDetailsResponseTimeChartComponent(responseTimesSuccess: Series[DateTime, Int], responseTimesFailures: Series[DateTime, Int], allActiveSessions: Series[DateTime, Int]): Component = throw new UnsupportedOperationException
	def getRequestDetailsScatterChartComponent(successData: Series[Int, Long], failuresData: Series[Int, Long]): Component = throw new UnsupportedOperationException
	def getRequestDetailsIndicatorChartComponent(columnSeries: Series[String, Int], pieSeries: Series[String, Int]): Component = throw new UnsupportedOperationException

}