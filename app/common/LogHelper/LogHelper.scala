package common.LogHelper


import common.DateHelper.DateHelper
import common.FileHelper.FileHelper


object LogHelper {
    private def writeLoger(logFile: String, data: String): Boolean =  {
        if (FileHelper.fileIsExist(logFile)) {
            return FileHelper.wirteFileApend(logFile, data);
        }else {
            return FileHelper.save2File(logFile, data);
        }
        return false;
    }

    def errLoger(data: String): Boolean = {
        val file = s"./runLog/errorLog-${DateHelper.getDate2Format()}.log";
        val time = DateHelper.getTimeOfDtae2Format();
        val log = s"error: {\n\ttime: ${time}\n\tdata: ${data}}\n";
        return writeLoger(file, log);
    }

    def warningLoger(data: String): Boolean = {
        val file = s"./runLog/warningLog-${DateHelper.getDate2Format()}.log";
        val time = DateHelper.getTimeOfDtae2Format();
        val log = s"warning: {\n\ttime: ${time}\n\tdata: ${data}}\n";
        return writeLoger(file, log);
    }

    def infoLoger(data: String): Boolean = {
        val file = s"./runLog/infoLog-${DateHelper.getDate2Format()}.log";
        val time = DateHelper.getTimeOfDtae2Format();
        val log = s"info: {\n\ttime: ${time}\n\tdata: ${data}}\n";
        return writeLoger(file, log);
    }
}
