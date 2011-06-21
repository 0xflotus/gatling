package com.excilys.ebi.gatling.core.action

import com.excilys.ebi.gatling.core.context.Context
import com.excilys.ebi.gatling.core.action.request.AbstractRequest
import com.excilys.ebi.gatling.core.capture.AbstractCapture
import com.excilys.ebi.gatling.core.assertion.AbstractAssertion
import com.excilys.ebi.gatling.core.processor.Processor

abstract class RequestAction(next: Action, request: AbstractRequest, givenProcessors: Option[List[Processor]]) extends AbstractAction {
  def execute(context: Context)
}