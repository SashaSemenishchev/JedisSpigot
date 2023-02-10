package me.mrfunny.jedispigot;

import org.bukkit.plugin.java.JavaPlugin;
import redis.clients.jedis.Jedis;

import java.util.LinkedList;
import java.util.List;

public final class JedisSpigot extends JavaPlugin {
    private final List<JedisConnection> connections = new LinkedList<>();
    private static JedisSpigot instance;
    @Override
    public void onLoad() {
        instance = this;
    }

    @Override
    public void onDisable() {
        for(JedisConnection connection : connections) {
            connection.close();
        }
    }

    public static JedisConnection getForPlugin(JavaPlugin plugin) {
        return getForPlugin(plugin, "localhost", 6379);
    }

    public static JedisConnection getForPlugin(JavaPlugin plugin, String host, int port) {
        JedisConnection connection = new JedisConnection(plugin, host, port);
        instance.connections.add(connection);
        return connection;
    }
}
