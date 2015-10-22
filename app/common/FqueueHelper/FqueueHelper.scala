/*
 * Copyright (c) 2015, BoDao, Inc. All Rights Reserved.
 *
 * Author@ dgl
 *
 * This software is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
 * CONDITIONS OF ANY KIND, either express or implied.
 *
 *
 * you can operate fqueue Conveniently by this software.
 *
 * open fqueue connect
 * get record in fqueue
 * close fqueue connect
 *
 */


package common.FqueueHelper

import net.rubyeye.xmemcached.XMemcachedClientBuilder
import net.rubyeye.xmemcached.utils.AddrUtil
import net.rubyeye.xmemcached.MemcachedClient

import config.DynConfiguration


object FqueueHelper {
    var fqueue: Option[FqueueTools] = None;
    val dynconf = DynConfiguration.getConf();

    def getFqueue(): FqueueTools = {
        fqueue match {
            case None =>
                val qhost = dynconf.getString("FQueue.Address");
                fqueue = Some(new FqueueTools(qhost));
                fqueue.get;
            case Some(queue) => queue
        }
    }
}


class FqueueTools(qhosts: String) {
    val builder = new XMemcachedClientBuilder(AddrUtil.getAddresses(qhosts));
    builder.setConnectionPoolSize(4);
    builder.setConnectTimeout(6000);
    val client: MemcachedClient = builder.build();

    def getQueue(qName: String): Option[String] = {
        val s = client.get[String](qName);
        if (s == null || s.equals("null")) return None;
        else Some(s);
    }

    def sendQueue(qName: String, data: String): Boolean =  {
        return client.set(qName, 0, data);
    }

    def close(): Unit = {
        try{
            this.client.shutdown();
        } catch {
            case e: Exception =>
        }
    }
}
