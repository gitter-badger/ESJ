package common.KafkaHelper


import scala.collection.mutable.Map

import kafka.producer.ProducerConfig
import kafka.producer.Producer
import kafka.producer.KeyedMessage
import java.util.Properties
import java.util._;

import config.DynConfiguration
import common.LogHelper.LogHelper
import common.FileHelper.FileHelper


class KafkaProducerHelper(kafkaConf: String) {
    val kafkaConf_kv = FileHelper.readFile_kv(kafkaConf, "=");
    val producer = initProducer(kafkaConf_kv);

    private def createProperties(props: Map[String, String]): Properties = {
        val propObj = new Properties();
        props.keys.foreach { prop =>
            propObj.put(prop, props(prop));
        }

        return propObj;
    }

    private def setProperties(propObj: Properties, props: Map[String, String]): Unit = {
        props.keys.foreach { prop =>
            propObj.put(props, props(prop));
        }
    }

    private def initProducer(props: Map[String, String]): Producer[String, String] = {
        val propObj = createProperties(props);
        val config = new ProducerConfig(propObj);
        val producer = new Producer[String, String](config);

        return producer;
    }

    def sendMsg(topic: String, msg: String): Unit = {
        try {
            val data = new KeyedMessage[String, String](topic, msg);
            producer.send(data);
        } catch {
            case ex: Exception =>
                println(ex.getMessage);
        }
    }
}


object KafkaProducerHelper {
    val kafkaConf = FileHelper.readFile_kv("./DynConfig/kafka.conf", "=");
    val producer = initProducer(kafkaConf);

    private def createProperties(props: Map[String, String]): Properties = {
        val propObj = new Properties();
        props.keys.foreach { prop =>
            propObj.put(prop, props(prop));
        }

        return propObj;
    }

    private def setProperties(propObj: Properties, props: Map[String, String]): Unit = {
        props.keys.foreach { prop =>
            propObj.put(props, props(prop));
        }
    }

    private def initProducer(props: Map[String, String]): Producer[String, String] = {
        val propObj = createProperties(props);
        val config = new ProducerConfig(propObj);
        val producer = new Producer[String, String](config);

        return producer;
    }

    def sendMsg(topic: String, msg: String): Unit = {
        try {
            val data = new KeyedMessage[String, String](topic, msg);
            producer.send(data);
        } catch {
            case ex: Exception =>
                println(ex.getMessage);
        }
    }
}
