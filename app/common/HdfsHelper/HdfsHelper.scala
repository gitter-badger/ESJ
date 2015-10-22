/*
 * Copyright (c) 2015, BoDao, Inc. All Rights Reserved.
 *
 * Author@ dgl
 *
 * This software is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
 * CONDITIONS OF ANY KIND, either express or implied.
 *
 * you can operate hdfs Conveniently by this software.
 *
 * you can read a hdfs's file to string or a file in local
 * you can wirte a string or a local file to hdfs
 *
 */


package common.HdfsHelper


import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import org.apache.hadoop.conf.{ Configuration => HConfiguration }
import org.apache.hadoop.fs.FSDataInputStream
import org.apache.hadoop.fs.FSDataOutputStream
import org.apache.hadoop.fs.FileStatus
import org.apache.hadoop.fs.FileSystem
import org.apache.hadoop.fs.Path
import org.apache.log4j.Logger
import play.api.Play
import scala.io._
import java.io.File
import java.io.FileOutputStream
import org.apache.hadoop.io.IOUtils
import java.io.FileInputStream
import config.SysConfiguration


object HdfsHelper {
    val hconf = new HConfiguration();
    hconf.addResource("hdfs-site.xml");
    val conf = SysConfiguration.getConf();
    System.setProperty("HADOOP_USER_NAME", conf.getString("hdfs.user"));

    def read2String(targetFile: String): String = {
        val result = new StringBuffer();
        val path = new Path(targetFile);
        val hdfs = FileSystem.get(hconf);
        hdfs.exists(path) match {
            case false =>
                throw new IOException(s"The file ${targetFile} is not exists, read ${targetFile} failed!");
            case true =>
                val reader = Source.fromInputStream(hdfs.open(path));
                for (line <- reader) result.append(line);
                reader.close();
        }
        hdfs.close();
        result.toString();
    }

    def read2File(targetFile: String, distFile: String): String = {
        val target = new Path(targetFile);
        val dist = new File(distFile);
        val hdfs = FileSystem.get(hconf);
        dist.exists match {
            case true => throw new IOException(s"The dist file ${targetFile} is exists!");
            case false =>
            hdfs.exists(target) match {
                case false => throw new IOException(s"The file ${targetFile} is not exists, read ${targetFile} failed!");
                case true =>
                    val fin = hdfs.open(target);
                    val fout = new FileOutputStream(dist);
                    IOUtils.copyBytes(fin, fout, hconf, true);
                    fin.close();
                    fout.close();
            }
        }
        hdfs.close();
        distFile;
    }

    /* use date format to create file, and opy the localFile to hdfs */
    def write2Hdfs(localFile: File, dir: String, timeStr: String, isDirUseTime: Boolean = false): Boolean = {
        try {
            System.setProperty("HADOOP_USER_NAME", conf.getString("hdfs.user"))
            val fs = FileSystem.get(hconf);
            val items = timeStr.split("-");
            val year = items(0);
            val month = items(1);
            val day = items(2);
            val fis = new FileInputStream(localFile);
            var distPath = new Path(s"${dir}/${timeStr}.log");
            if (isDirUseTime) {
                distPath = new Path(s"${dir}/${year}/${month}/${day}/${timeStr}.log");
            }
            val os = fs.create(distPath);
            IOUtils.copyBytes(fis, os, 4096, true);
            os.close();
            fis.close();
            fs.close();
            return true;
        } catch {
            case ex: Exception =>
                return false;
        }

        return false;
    }

    /* copy the localFile to Hdfs, the dis file is fileName */
    def write2Hdfs(localFile: File, fileName: String): Boolean = {
        try {
            System.setProperty("HADOOP_USER_NAME", conf.getString("hdfs.user"));
            val fs = FileSystem.get(hconf);
            val fis = new FileInputStream(localFile);
            var distPath = new Path(fileName);
            val os = fs.create(distPath);
            IOUtils.copyBytes(fis, os, 4096, true);
            os.close();
            fis.close();
            fs.close();
        } catch {
            case ex: Exception =>
                return false;
        }

        return true;
    }

    /* copy the localFile to Hdfs, the dis file is fileName */
    def write2Hdfs(localFile: String, fileName: String): Boolean = {
        try {
            System.setProperty("HADOOP_USER_NAME", conf.getString("hdfs.user"));
            val fs = FileSystem.get(hconf);
            val fis = new FileInputStream(new File(localFile));
            var distPath = new Path(fileName);
            val os = fs.create(distPath);
            IOUtils.copyBytes(fis, os, 4096, true);
            os.close();
            fis.close();
            fs.close();
            return true;
        } catch {
            case ex: Exception =>
                println(ex.getMessage);
                return false;
        }

        return false;
    }
}
