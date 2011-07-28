package com.excilys.ebi.gatling.core.feeder

import scala.io.Source
import scala.collection.immutable.HashMap
import scala.collection.mutable.Queue

class SeparatedValuesFeeder(filePath: String, mappings: List[String], val separator: String) extends Feeder(filePath, mappings) {

  var currentUser = 0
  var seeds: Queue[Map[String, String]] = Queue()

  for (line <- Source.fromFile("user-files/seeds/" + filePath + ".tsv", "utf-8").getLines) {
    var lineMap = new HashMap[String, String]

    for (mapping <- mappings zip line.split(separator).toList)
      lineMap = lineMap + mapping

    seeds = seeds += lineMap
  }

  logger.debug("Feeder Seeds: {}", seeds)

  def next: Map[String, String] = seeds.dequeue
}