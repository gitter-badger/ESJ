package business

import scala.collection.mutable.Map
import java.util.concurrent.locks.ReentrantReadWriteLock

import common.FileHelper.FileHelper
import config.DynConfiguration
import common.LogHelper.LogHelper
import scala.collection.mutable.Map


object CodeMapApp {
    val dynConf = DynConfiguration.getConf();
    val codemaps = initCodeMaps();

    private def loadCodeMap(file: String, codemaps: Map[String, CodeMap], key: String): Boolean =  {
        val codemap = new CodeMap(file);
        if (codemaps.contains(key)) {
            codemaps(key) = codemap;
        }else {
            codemaps += (key -> codemap);
        }

        return true;
    }

    private def initCodeMaps(): Map[String, CodeMap] = {
        val codemaps = Map[String, CodeMap]();
        val dir = dynConf.getString("codemap.dir").trim().stripSuffix("/") + "/";
        val fileNames = FileHelper.getRegFileNameAtDir(dir);

        if (fileNames.size <= 0) LogHelper.warningLoger(s"code map files not found at ${dir}");
        else {
            for (fileName <- fileNames) {
                val file = dir + fileName;
                loadCodeMap(file, codemaps, fileName);
            }
        }

        return codemaps;
    }

    def reloadCodeMap(): Boolean =  {
        codemaps.keys.foreach { codemapKey =>
            val codemap = codemaps(codemapKey);
            if (!codemap.reloadCodeMap(codemap.getCodeMapFile())) {
                LogHelper.errLoger(s"CodeMapApp reload code map err at ${codemap.getCodeMapFile()}");
            }
        }
        return true;
    }

    def getCodeMap(codemapKey: String): CodeMap = {
        if (codemaps.contains(codemapKey)) {
            return codemaps(codemapKey);
        }else {
            return null;
        }
    }
}
