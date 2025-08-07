package com.wenge.model.workflow.util;
 
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import org.apache.commons.lang3.concurrent.BasicThreadFactory;

/**
 * 一个简单的定时清理key的Map
 */
public class ThrottlingPool {
    private static final Map<String, Entity> map = new ConcurrentHashMap<>();
    private static final ScheduledExecutorService executor = new ScheduledThreadPoolExecutor(1, (new BasicThreadFactory.Builder()).namingPattern("throttling-pool-%d").daemon(true).build());
 
    public ThrottlingPool() {
    }
 
    public static synchronized void put(String key, Object data) {
        put(key, data, -1);
    }
 
    public static synchronized void put(final String key, Object data, int expire) {
        remove(key);
        if (data != null) {
            if (expire >= 0) {
                Future future = executor.schedule(new Runnable() {
                    public void run() {
                        synchronized(ThrottlingPool.class) {
                            ThrottlingPool.map.remove(key);
                        }
                    }
                }, (long)expire, TimeUnit.MILLISECONDS);
                map.put(key, new Entity(data, expire, future));
            } else {
                map.put(key, new Entity(data, expire, null));
            }
        }
 
    }
 
    public static synchronized Object get(String key) {
        Entity entity = (Entity)map.get(key);
        return entity != null ? entity.getValue() : null;
    }
 
    public static synchronized <T> T get(String key, Class<T> clazz) {
        return (T)clazz.cast(get(key));
    }
 
    public static synchronized int getExpire(String key) {
        Entity entity = (Entity)map.get(key);
        return entity != null ? entity.getExpire() : 0;
    }
 
    public static synchronized Object remove(String key) {
        Entity entity = (Entity)map.remove(key);
        if (entity == null) {
            return null;
        } else {
            Future future = entity.getFuture();
            if (future != null) {
                future.cancel(true);
            }
 
            return entity.getValue();
        }
    }
 
    public static synchronized int size() {
        return map.size();
    }
 
    public static synchronized void clear() {
        for(Entity entity : map.values()) {
            if (entity != null) {
                Future future = entity.getFuture();
                if (future != null) {
                    future.cancel(true);
                }
            }
        }
 
        map.clear();
    }
 
    public static synchronized Map<String, Entity> getPool() {
        return map;
    }
 
    private static class Entity {
        private Object value;
        private int expire;
        private Future future;
 
        public Entity(Object value, int expire, Future future) {
            this.value = value;
            this.expire = expire;
            this.future = future;
        }
 
        public Object getValue() {
            return this.value;
        }
 
        public int getExpire() {
            return this.expire;
        }
 
        public Future getFuture() {
            return this.future;
        }
    }
}