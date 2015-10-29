package common.LogHelper


import common.DateHelper.DateHelper
import common.FileHelper.FileHelper


object LogHelper {
    private def write(logFile: String, data: String): Boolean =  {
        if (FileHelper.fileIsExist(logFile)) {
             FileHelper.wirteFileApend(logFile, data)
        }else {
             FileHelper.save2File(logFile, data)
        }
        false
    }

    def err(data: String): Boolean = {
        val file = s"./logs/err-${DateHelper.getDate2Format()}.log"
        val time = DateHelper.getTimeOfDtae2Format()
        val log = s"error: {\n\ttime: ${time}\n\tdata: ${data}}\n"
        write(file, log)
    }

    def warn(data: String): Boolean = {
        val file = s"./logs/warn-${DateHelper.getDate2Format()}.log"
        val time = DateHelper.getTimeOfDtae2Format()
        val log = s"warning: {\n\ttime: ${time}\n\tdata: ${data}}\n"
        write(file, log)
    }

    def info(data: String): Boolean = {
        val file = s"./logs/info-${DateHelper.getDate2Format()}.log"
        val time = DateHelper.getTimeOfDtae2Format()
        val log = s"info: {\n\ttime: ${time}\n\tdata: ${data}}\n"
        write(file, log)
    }
}
