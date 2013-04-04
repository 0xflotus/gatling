/**
 * Copyright 2011-2013 eBusiness Information, Groupe Excilys (www.excilys.com)
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
package io.gatling.charts.template

import com.dongxiguo.fastring.Fastring.Implicits._

import io.gatling.charts.component.RequestStatistics
import io.gatling.charts.report.{ GroupContainer, RequestContainer }
import io.gatling.charts.report.Container.{ GROUP, REQUEST }
import io.gatling.core.util.FileHelper.formatToFilename
import io.gatling.core.util.StringHelper.escapeJsDoubleQuoteString

class StatsJsTemplate(stats: GroupContainer) {

	def getOutput: String = {

		def renderStatsRequest(request: RequestStatistics) = fast"""
name: "${escapeJsDoubleQuoteString(request.name)}",
path: "${escapeJsDoubleQuoteString(request.path)}",
pathFormatted: "${formatToFilename(request.path)}",
stats: {
    numberOfRequests : {
        total: "${request.numberOfRequestsStatistics.printableTotal}",
        ok: "${request.numberOfRequestsStatistics.printableSuccess}",
        ko: "${request.numberOfRequestsStatistics.printableFailure}"
    },
    minResponseTime : {
        total: "${request.minResponseTimeStatistics.printableTotal}",
        ok: "${request.minResponseTimeStatistics.printableSuccess}",
        ko: "${request.minResponseTimeStatistics.printableFailure}"
    },
    maxResponseTime : {
        total: "${request.maxResponseTimeStatistics.printableTotal}",
        ok: "${request.maxResponseTimeStatistics.printableSuccess}",
        ko: "${request.maxResponseTimeStatistics.printableFailure}"
    },
    meanResponseTime : {
        total: "${request.meanStatistics.printableTotal}",
        ok: "${request.meanStatistics.printableSuccess}",
        ko: "${request.meanStatistics.printableFailure}"
    },
    standardDeviation : {
        total: "${request.stdDeviationStatistics.printableTotal}",
        ok: "${request.stdDeviationStatistics.printableSuccess}",
        ko: "${request.stdDeviationStatistics.printableFailure}"
    },
    percentiles1 : {
        total: "${request.percentiles1.printableTotal}",
        ok: "${request.percentiles1.printableSuccess}",
        ko: "${request.percentiles1.printableFailure}"
    },
    percentiles2 : {
        total: "${request.percentiles2.printableTotal}",
        ok: "${request.percentiles2.printableSuccess}",
        ko: "${request.percentiles2.printableFailure}"
    },
    group1 : {
        name: "${request.groupedCounts(0).name}",
        count: ${request.groupedCounts(0).count},
        percentage: ${request.groupedCounts(0).percentage}
    },
    group2 : {
        name: "${request.groupedCounts(1).name}",
        count: ${request.groupedCounts(1).count},
        percentage: ${request.groupedCounts(1).percentage}
    },
    group3 : {
        name: "${request.groupedCounts(2).name}",
        count: ${request.groupedCounts(2).count},
        percentage: ${request.groupedCounts(2).percentage}
    },
    group4 : {
        name: "${request.groupedCounts(3).name}",
        count: ${request.groupedCounts(3).count},
        percentage: ${request.groupedCounts(3).percentage}
    },
    meanNumberOfRequestsPerSecond: {
        total: "${request.meanNumberOfRequestsPerSecondStatistics.printableTotal}",
        ok: "${request.meanNumberOfRequestsPerSecondStatistics.printableSuccess}",
        ko: "${request.meanNumberOfRequestsPerSecondStatistics.printableFailure}"
    }
}
"""

		def renderStatsGroup(group: GroupContainer): String = fast"""
type: "$GROUP",
contents: {
${
			(group.contents.values.map {
				_ match {
					case subGroup: GroupContainer => renderStatsGroup(subGroup)
					case request: RequestContainer => fast""""${formatToFilename(request.name)}": {
        type: "${REQUEST}",
        ${renderStatsRequest(request.stats)}
    }"""
				}
			}).mkFastring(",")
		}
},
${renderStatsRequest(group.requestStats)}
""".toString

		fast"""
var stats = {
    ${renderStatsGroup(stats)}
}

function fillStats(stat){
    $$("#numberOfRequests").append(stat.numberOfRequests.total);
    $$("#numberOfRequestsOK").append(stat.numberOfRequests.ok);
    $$("#numberOfRequestsKO").append(stat.numberOfRequests.ko);

    $$("#minResponseTime").append(stat.minResponseTime.total);
    $$("#minResponseTimeOK").append(stat.minResponseTime.ok);
    $$("#minResponseTimeKO").append(stat.minResponseTime.ko);

    $$("#maxResponseTime").append(stat.maxResponseTime.total);
    $$("#maxResponseTimeOK").append(stat.maxResponseTime.ok);
    $$("#maxResponseTimeKO").append(stat.maxResponseTime.ko);

    $$("#meanResponseTime").append(stat.meanResponseTime.total);
    $$("#meanResponseTimeOK").append(stat.meanResponseTime.ok);
    $$("#meanResponseTimeKO").append(stat.meanResponseTime.ko);

    $$("#standardDeviation").append(stat.standardDeviation.total);
    $$("#standardDeviationOK").append(stat.standardDeviation.ok);
    $$("#standardDeviationKO").append(stat.standardDeviation.ko);

    $$("#percentiles1").append(stat.percentiles1.total);
    $$("#percentiles1OK").append(stat.percentiles1.ok);
    $$("#percentiles1KO").append(stat.percentiles1.ko);

    $$("#percentiles2").append(stat.percentiles2.total);
    $$("#percentiles2OK").append(stat.percentiles2.ok);
    $$("#percentiles2KO").append(stat.percentiles2.ko);

    $$("#meanNumberOfRequestsPerSecond").append(stat.meanNumberOfRequestsPerSecond.total);
    $$("#meanNumberOfRequestsPerSecondOK").append(stat.meanNumberOfRequestsPerSecond.ok);
    $$("#meanNumberOfRequestsPerSecondKO").append(stat.meanNumberOfRequestsPerSecond.ko);
}
""".toString
	}
}
