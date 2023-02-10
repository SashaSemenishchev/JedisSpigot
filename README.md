# JedisSpigot
A non-blocking Jedis wrapper for Spigot plugins

Use this snippet to get a connection 
```java
// in MyPlugin.java
@Override
public void onEnable() {
  JedisConnection connectioon = JedisSpigot.getForPlugin(this);
}
```
