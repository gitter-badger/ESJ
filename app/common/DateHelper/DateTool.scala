/*
 * Copyright (c) 2015, BoDao, Inc. All Rights Reserved.
 *
 * Author@ dgl
 *
 * This software is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
 * CONDITIONS OF ANY KIND, either express or implied.
 *
 *
 * you can operate file Conveniently by this software.
 *
 * apend data to file
 * get file name at dir
 * clean file content
 * get process running dir
 * etc.
 *
 */

package common.DateHelper

import java.text.SimpleDateFormat

class Date(year: String, mon: String, day: String, hour: String) {
  	def getYear = year;
  	def getMon = mon;
  	def getDay = day;
  	def gethour = hour;
}

object DateHelper {
	val formatDay = new SimpleDateFormat("yyyy-MM-dd")
	val formatSecond = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")

	/**
	 * ts : ms
	 */

	def formatDate2Day(ts: Long): String = formatDay.format(ts)
	def formatDate2Seconds(ts: Long): String = formatSecond.format(ts)

    /* format like that yyyy-MM-dd */
	def getDate2Format(): String = {
		val currTime = System.currentTimeMillis;
		val date = formatDate2Day(currTime);
		return date;
	}

    def getCurrentTimeMillis(): Long = {
        return System.currentTimeMillis;
    }

    def getCurrentTimeSeconds(): Long = {
        return System.currentTimeMillis / 1000;
    }

    /* remoce the '-' in date */
	def date2String(date: String): String = {
		val fields = date.trim.split("-", 3);
		var ret = "";
		for (f <- fields) {
			ret += f.trim;
		}

		return ret;
	}

    def getHourOfDate2Format(): String = {
        val currTime = System.currentTimeMillis;
        val date = formatDate2Seconds(currTime);

        val fields = date.split(":", 2);
        return fields(0);
    }

    def getTimeOfDtae2Format(): String = {
        val currTime = System.currentTimeMillis;
        val date = formatDate2Seconds(currTime);

        return date;
    }

	private def getNDayAgo(n: Int): Date = {
		val currTime = System.currentTimeMillis - (n * 86400000);
		val time = formatDate2Day(currTime);
		val ret = time.toString.trim.split("-", 3);
		val date = new Date(ret(0), ret(1), ret(2), Unit.toString());
		date;
	}

	def getDayAgo(n: Int) = getNDayAgo(n);

	def getDayAgo2String(n: Int): String = {
		val date =  getDayAgo(n);
		val dateStr = date.getYear + date.getMon + date.getDay;
		return dateStr;
	}

	def getDayAgo2Format(n: Int): String = {
		val date =  getDayAgo(n);
		val dataFormat = date.getYear + "-" + date.getMon + "-" + date.getDay;
		return dataFormat;
	}
}
