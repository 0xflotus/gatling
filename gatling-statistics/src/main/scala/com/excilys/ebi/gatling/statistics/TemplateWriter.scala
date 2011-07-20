package com.excilys.ebi.gatling.statistics

import java.io.File
import java.io.FileWriter

class TemplateWriter(val runOn: String, val fileName: String) {
  def writeToFile(output: String) = {
    val dir = new File(runOn)
    dir.mkdir
    val file = new File(dir, fileName)
    val fw = new FileWriter(file)
    fw.write(output)
    fw.close
  }
}