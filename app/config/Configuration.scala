/*
 * Copyright (c) 2015, BoDao, Inc. All Rights Reserved.
 *
 * Author@ dgl
 *
 * This software is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
 * CONDITIONS OF ANY KIND, either express or implied.
 *
 * load the Program config file.
 * here tow config for this program.
 * SysConfiguration: is the play2's config.
 * DynConfiguration: is Dynamic config for this program.
 * DynConfiguration Become effective was Do not need reCompile the program.
 *
 */

package config

import com.typesafe.config.Config
import com.typesafe.config.ConfigFactory
import common.FileHelper.FileHelper
import scala.collection.mutable.ArrayBuffer
import scala.collection.mutable.Map
import common.FileHelper.FileHelper

object SysConfiguration {
	var conf: Option[Config] = None

	def getConf(): Config = {
	  conf match {
	    case None =>
	    	val conf_ = ConfigFactory.load("application.conf");
	    	conf = Some(conf_)
	    	conf_
	    case Some(conf) =>
	    	conf
	  }
	}
}


class DynConfig(kvs: ArrayBuffer[String]) {
	val conf = Map[String, String]();
	initConfing(kvs)

	def getString(key: String): String = {
		if (conf.contains(key)) return conf(key).toString;
		else Unit.toString;
	}

	private def rmQuotes(str: String): String = {
		if (str.length > 0) {
			if (str.charAt(0) == '\"' && str.charAt(str.length - 1) == '\"')
				return str.substring(1, str.length - 1);
			else str;
		}else return str;
	}

	private def initConfing(kvs: ArrayBuffer[String]) {
		kvs.foreach { kv =>
			if (kv.trim.length > 0 && kv.trim.charAt(0) != '#') {	/* remove '\n' and notation lines */
				val fields = kv.trim().split("=", 2);
				if (!conf.contains(fields(0).trim())) {
					conf += (fields(0).trim -> rmQuotes(fields(1).trim()));
				}
			}
		}
	}
}


object DynConfigFactory {
	def load(configFile: String): DynConfig = {
		if (!FileHelper.fileIsExist(configFile))
			return new DynConfig(new ArrayBuffer[String]());	/* null */
		else {
			val lines = FileHelper.getFileLinesTrim(configFile);
			return new DynConfig(lines);
		}
	}
}


object DynConfiguration {
    var conf: Option[DynConfig] = None;
	def getConf(): DynConfig = {
	  conf match {
	    case None =>
	    	val conf_ = DynConfigFactory.load("./DynConfig/DynamicApplication.conf");
	    	conf = Some(conf_);
	    	conf_;
	    case Some(conf) => conf;
	  }
	}
}
