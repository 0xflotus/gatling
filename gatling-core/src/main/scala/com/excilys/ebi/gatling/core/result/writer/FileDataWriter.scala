package com.excilys.ebi.gatling.core.result.writer

import com.excilys.ebi.gatling.core.action.EndAction._
import com.excilys.ebi.gatling.core.result.message.ActionInfo
import com.excilys.ebi.gatling.core.result.message.InitializeDataWriter
import com.excilys.ebi.gatling.core.util.PathHelper._
import java.io.FileOutputStream
import java.io.File
import java.io.BufferedOutputStream
import java.io.OutputStreamWriter
import org.apache.commons.lang3.time.FastDateFormat
import org.apache.commons.lang3.StringUtils
import java.util.concurrent.CountDownLatch

class FileDataWriter extends DataWriter {
  var osw: OutputStreamWriter = null
  var latch: CountDownLatch = null

  var numberOfUsers = 0
  var numberOfUsersDone = 0
  var runOn = StringUtils.EMPTY

  val formatter: FastDateFormat = FastDateFormat.getInstance("yyyy-MM-dd HH:mm:ss")
  val fileNameFormatter = FastDateFormat.getInstance("yyyyMMddHHmmss")

  def receive = {
    case ActionInfo(scenarioName, userId, action, executionStartDate, executionDuration, resultStatus, resultMessage) ⇒ {
      val strBuilder = new StringBuilder
      strBuilder.append(runOn).append("\t")
        .append(scenarioName).append("\t")
        .append(userId).append("\t")
        .append(action).append("\t")
        .append(formatter.format(executionStartDate)).append("\t")
        .append(executionDuration).append("\t")
        .append(resultStatus).append("\t")
        .append(resultMessage).append("\n")

      osw.write(strBuilder.toString)
      if (action == END_OF_SCENARIO)
        numberOfUsersDone += 1
      if (numberOfUsers == numberOfUsersDone && self.dispatcher.mailboxSize(self) == 0) {
        osw.flush
        osw.close
        latch.countDown
      }
    }
    case InitializeDataWriter(runOn, numberOfUsers, latch) ⇒ {
      val dir = new File(GATLING_RESULTS_FOLDER + "/" + fileNameFormatter.format(runOn))
      dir.mkdir
      val file = new File(dir, GATLING_SIMULATION_LOG_FILE)

      osw = new OutputStreamWriter(new BufferedOutputStream(new FileOutputStream(file, true)))
      this.runOn = formatter.format(runOn)
      this.numberOfUsers = numberOfUsers
      this.latch = latch
    }
  }
}