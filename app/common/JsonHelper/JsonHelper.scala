package common.JsonHelper


import net.liftweb.json.JString
import net.liftweb.json.JsonAST.JInt
import rapture.json._
import rapture.json.jsonBackends.lift._

import scala.collection.mutable.Map


object JsonHelper {
    def test(): Unit = {
        var js = json"""{
    "item": {
        "i1": [
            1,
            2,
            3
        ],
        "i2": [
            2,
            3,
            4
        ]
    },
    "page": {
        "p1": [
            1,
            2,
            3
        ],
        "p2": [
            4,
            5,
            6
        ]
    }
}
""";
        // for (i <- 0 to 5) {
        //     val jsStr = s"""{"${i}": [1,2,2]}""";
        //     val jsObj = Json.parse(jsStr);
        //     js = js ++ jsObj;
        // }

        val hh = js.as[scala.collection.immutable.Map[String, Json]];
		hh.keys.foreach { jj =>
				println(jj + ": ");
			val g = hh(jj);
			val tt = g.as[scala.collection.immutable.Map[String, Json]];
			tt.keys.foreach { k =>
					print(k + ":");
					var m = "";
					val p = tt(k).as[Array[Any]];
					for (q <- p) {
						if (q.isInstanceOf[JInt]) {
							val x = q.asInstanceOf[JInt];
							m += (x.values + ",");
						}else {
							val x = q.asInstanceOf[JString];
							m += (x.values + ",");
						}
					}
					m = m.substring(0, m.length - 1);
					println(m);
			}

		}

        // println(Json.format(js));
    }

    def map2Json(map: Map[String, String]): Json = {
        var jsObj = json"""{}""";

        map.keys.foreach { key =>
            val jsStr = s"""{ "${key}": "${map(key)}" }""";
            val js = Json.parse(jsStr);
            jsObj = jsObj ++ js;
        }

        println(jsObj);
        return jsObj;
    }
}
