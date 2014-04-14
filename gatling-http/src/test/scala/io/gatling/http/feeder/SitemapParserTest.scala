/**
 * Copyright 2011-2014 eBusiness Information, Groupe Excilys (www.ebusinessinformation.fr)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.gatling.http.feeder

import reflect.io.File

import io.gatling.core.feeder.Record
import io.gatling.core.config._

import org.junit.runner.RunWith

import org.specs2.mutable.Specification
import org.specs2.runner.JUnitRunner

import org.mockito.Mockito._
import org.mockito.Matchers._
import org.specs2.mock.mockito.CalledMatchers
import java.io.InputStream
import java.io.IOException

/**
 * @author Ivan Mushketyk
 */
@RunWith(classOf[JUnitRunner])
class SitemapParserTest extends Specification with CalledMatchers {

  def getFile(filePath: String) = File(getClass.getClassLoader.getResource("sitemap.xml").getFile)

  def getIs(filePath: String) = getClass.getClassLoader.getResourceAsStream(filePath)

  "sitemap parser" should {
    "parse valid sitemap input stream" in {
      val records = SitemapParser.parse(getIs("sitemap.xml")).toArray

      verifySitemapRecords(records)
    }

    "parse valid sitemap file" in {
      val resource = FileResource(getFile("sitemap.xml"))
      val records = SitemapParser.parse(resource).toArray

      verifySitemapRecords(records)
    }

    "input stream is closed on error" in {
      val fileIs = mock(classOf[InputStream])
      val resource = org.mockito.Mockito.mock(classOf[Resource])
      when(resource.inputStream).thenReturn(fileIs)
      when(fileIs.read()).thenThrow(classOf[IOException])
      when(fileIs.read(any(classOf[Array[Byte]]))).thenThrow(classOf[IOException])
      when(fileIs.read(any(classOf[Array[Byte]]), anyInt, anyInt)).thenThrow(classOf[IOException])

      SitemapParser.parse(resource).toArray must throwA[IOException]
    }

    "throw exception when loc is missing" in {
      SitemapParser.parse(getIs("sitemap_loc_missing.xml")) must throwA[SitemapFormatException]
    }

    "throw exception when loc is missing" in {
      SitemapParser.parse(getIs("sitemap_no_value.xml")) must throwA[SitemapFormatException]
    }

      def verifySitemapRecords(records: Array[Record[String]]) = {
        records.size should be equalTo 5

        records(0) should be equalTo Map(
          "loc" -> "http://www.example.com/",
          "lastmod" -> "2005-01-01",
          "changefreq" -> "monthly",
          "priority" -> "0.8")

        records(1) should be equalTo Map(
          "loc" -> "http://www.example.com/catalog?item=12&amp;desc=vacation_hawaii",
          "changefreq" -> "weekly")

        records(2) should be equalTo Map(
          "loc" -> "http://www.example.com/catalog?item=73&amp;desc=vacation_new_zealand",
          "lastmod" -> "2004-12-23",
          "changefreq" -> "weekly")

        records(3) should be equalTo Map(
          "loc" -> "http://www.example.com/catalog?item=74&amp;desc=vacation_newfoundland",
          "lastmod" -> "2004-12-23T18:00:15+00:00",
          "priority" -> "0.3")

        records(4) should be equalTo Map(
          "loc" -> "http://www.example.com/catalog?item=83&amp;desc=vacation_usa",
          "lastmod" -> "2004-11-23")
      }

  }

}
