package common.ErrorHelper


import scala.collection.mutable.ArrayBuffer

object ErrorHelper {
    val errors = ArrayBuffer[String]();

    def errorLog(err: String): Unit = {
        errors += err;
    }

    def getError(): String = {
        var errorStr = "";
        for (e <- errors) {
            errorStr += (e + "\n");
        }

        if (errorStr != "") errorStr = errorStr.substring(0, errorStr.length - 1);

        return errorStr;
    }

    def cleanErrors(): Unit = {
        if (errors.size > 0) errors.trimEnd(errors.size);
    }
}
