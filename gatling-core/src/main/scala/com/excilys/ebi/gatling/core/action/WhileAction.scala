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
package com.excilys.ebi.gatling.core.action
import com.excilys.ebi.gatling.core.context.Context

/**
 * Represents a While in the scenario.
 *
 * @constructor creates a While loop in the scenario
 * @param testFunction the function that will be used to decide when to stop the loop
 * @param nextTrue the chain executed if testFunction evaluates to true, passed as a Function for construct time
 * @param nextAfter the chain executed if testFunction evaluates to false
 */
class WhileAction(testFunction: Context => Boolean, var nextTrue: WhileAction => Action, nextAfter: Action) extends Action {

	val nextTrueAction = nextTrue.apply(this)

	/**
	 * Evaluates the testFunction and if true executes the first action of nextTrue
	 * else it executes the first action of nextAfter
	 *
	 * @param context Context for current user
	 * @return Nothing
	 */
	def execute(context: Context) = {

		if (testFunction.apply(context)) {
			nextTrueAction.execute(context)
		} else {
			context.resetWhileDuration
			nextAfter.execute(context)
		}
	}
}