package me.mrfunny.jedispigot;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import redis.clients.jedis.Jedis;

import java.io.Closeable;
import java.nio.charset.StandardCharsets;
import java.util.LinkedList;
import java.util.List;

public class JedisConnection implements Closeable {
    private final int port;
    private final String host;
    private final JavaPlugin plugin;
    private Jedis jedisReceive = null;
    private Jedis jedisSend;
    private final List<RegisteredJedisSubscriber> subscribers = new LinkedList<>();

    private final Object receiveLock = new Object();
    private Thread receiveThread;

    JedisConnection(JavaPlugin plugin, String host, int port) {
        this.host = host;
        this.port = port;

        Thread sendThread = new Thread(() -> {
            this.jedisSend = new Jedis(this.host, this.port);
        });
        sendThread.setDaemon(true);
        sendThread.start();
        this.plugin = plugin;
    }

    public void handle(JedisMessageHandler handler, String... channels) {
        Thread newThread = new Thread(() -> {
            synchronized (receiveLock) {
                if(jedisReceive != null) {
                    for (RegisteredJedisSubscriber subscriber : subscribers) {
                        subscriber.unsubscribe();
                    }
                    jedisReceive.close();
                }
                this.jedisReceive = new Jedis(host, port);
                byte[][] channelsToPass = new byte[channels.length][];
                for (int i = 0; i < channels.length; i++) {
                    channelsToPass[i] = channels[i].getBytes(StandardCharsets.UTF_8);
                }
                RegisteredJedisSubscriber subscriber = new RegisteredJedisSubscriber(handler);
                subscribers.add(subscriber);
                jedisReceive.subscribe(subscriber, channelsToPass);
            }
        });
        newThread.start();
        newThread.setDaemon(true);
        receiveThread.interrupt();
        receiveThread = newThread;
    }

    public void emit(String channel, byte[] data) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
           this.jedisSend.publish(channel.getBytes(StandardCharsets.UTF_8), data);
        });
    }

    @Override
    public void close() {
        jedisSend.close();
        for(RegisteredJedisSubscriber subscriber : subscribers) {
            subscriber.unsubscribe();
        }
        if(jedisReceive != null) {
            jedisReceive.close();
        }
    }
}
