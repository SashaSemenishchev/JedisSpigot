package me.mrfunny.jedispigot;

import redis.clients.jedis.JedisPubSub;

public interface JedisMessageHandler {
    void onMessage(String channel, byte[] message);
}
