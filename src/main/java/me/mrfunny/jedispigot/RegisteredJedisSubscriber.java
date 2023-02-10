package me.mrfunny.jedispigot;

import com.google.common.io.ByteStreams;
import redis.clients.jedis.BinaryJedisPubSub;
import redis.clients.jedis.JedisPubSub;

import java.nio.charset.StandardCharsets;

public class RegisteredJedisSubscriber extends BinaryJedisPubSub {
    private final JedisMessageHandler handler;

    public RegisteredJedisSubscriber(JedisMessageHandler handler) {
        this.handler = handler;
    }
    @Override
    public void onMessage(byte[] channel, byte[] message) {
        handler.onMessage(new String(channel, StandardCharsets.UTF_8), message);
    }
}
