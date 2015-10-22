/*
 * Copyright (c) 2015, BoDao, Inc. All Rights Reserved.
 *
 * Author@ dgl
 *
 * This software is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
 * CONDITIONS OF ANY KIND, either express or implied.
 *
 * this object keep the tags code map in mem
 * can reload the code map from file
 * will be update every day.
 *
 */


package business


import scala.collection.mutable.Map
import java.util.concurrent.locks.ReentrantReadWriteLock

import common.FileHelper.FileHelper
import common.LogHelper.LogHelper


class CodeMap(codemapFile: String) {
    val lock = new ReentrantReadWriteLock();
    val codeMap = loadCodeMap(codemapFile);

    def getCodeMapFile(): String =  {
        return codemapFile;
    }

    def readLock(): Unit = {
        lock.readLock().lock();
    }

    def writeLock(): Unit = {
        lock.writeLock().lock();
    }

    def readUnlock(): Unit = {
        lock.readLock().unlock();
    }

    def writeUnLock(): Unit = {
        lock.writeLock().unlock();
    }

    private def loadCodeMap(codemapFile: String): Map[String, String] = {
        return FileHelper.readFile_kv(codemapFile, "\t");
    }

    def getCodeMapSize(): Int = {
        readLock();
        val size = codeMap.size;
        readUnlock();
        return size;
    }

    def getCode(key: String): String = {
        if (codeMap.contains(key)) {
            return codeMap(key);
        }else return Unit.toString;
    }

    def getCodeSafety(key: String): String = {
        readLock();
        val code = getCode(key);
        readUnlock();
        return code;
    }

    def reloadCodeMap(codemapFile: String): Boolean = {
        val newmap = loadCodeMap(codemapFile);

        if (newmap.size <= 0) {
            LogHelper.errLoger("reload code map err at CodeMap, new map size was 0");
            return false;
        }

        writeLock();
        newmap.keys.foreach { key =>
            if (codeMap.contains(key)) {
                codeMap(key) = newmap(key);
            }else {
                codeMap += (key -> newmap(key));
            }
        }
        writeUnLock();

        LogHelper.infoLoger("reload code map success");

        return true;
    }
}
