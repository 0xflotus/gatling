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
package com.excilys.ebi.gatling.core.provider.capture

import com.ximpleware.CustomVTDGen
import com.ximpleware.VTDNav
import com.ximpleware.AutoPilot
import com.excilys.ebi.gatling.core.util.StringHelper._
import javax.xml.parsers.DocumentBuilderFactory
import java.io.InputStream
import org.jaxen.XPath
import org.jaxen.dom.DOMXPath
import org.w3c.dom.Node

object XPathCaptureProvider {
	private val factory = DocumentBuilderFactory.newInstance
	factory.setNamespaceAware(false) // Should be configurable as well as other features of this factory

	val parser = factory.newDocumentBuilder
}
/**
 * This class is a built-in provider that helps searching with XPath Expressions
 *
 * it requires a well formatted XML document, otherwise, it will throw an exception
 *
 * @constructor creates a new XPathCaptureProvider
 * @param xmlContent the XML document as bytes in which the XPath search will be applied
 */
class XPathCaptureProvider(xmlContent: InputStream) extends AbstractCaptureProvider {

	val document = XPathCaptureProvider.parser.parse(xmlContent)

	/**
	 * The actual capture happens here. The XPath expression is searched for and the first
	 * result is returned if existing.
	 *
	 * @param expression a String containing the XPath expression to be searched
	 * @return an option containing the value if found, None otherwise
	 */
	def capture(expression: Any): Option[String] = {
		logger.debug("[XPathCaptureProvider] Capturing with expression : {}", expression)

		val xpathExpression: XPath = new DOMXPath(expression.toString);

		val results = xpathExpression.selectNodes(document).asInstanceOf[java.util.List[Node]] // FIXME: Node is in org.w3c.dom. Which DOM implementation is the best ?

		val result = if (results.isEmpty())
			None
		else
			Some(results.get(0).getTextContent) // FIXME: one can choose which result to get

		logger.debug("XPATH CAPTURE: {}", result)
		result
	}
}