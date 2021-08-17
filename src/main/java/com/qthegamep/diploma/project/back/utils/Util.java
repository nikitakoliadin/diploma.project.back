package com.qthegamep.diploma.project.back.utils;

import redis.clients.jedis.Jedis;

public class Util {
    public static String getProminSession() {
        Jedis jedis = new Jedis(System.getProperty("acsk.redis.host"), Integer.parseInt(System.getProperty("acsk.redis.port")));
        return jedis.get("current.session.ekb");
    }
}
