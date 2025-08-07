package com.wg.appframe.core.lock;

import java.util.concurrent.TimeUnit;

/**
 * 操作redis对象
 */
public interface RedisLockHandle {

    /**
     * 根据key获取数据
     * @param key
     * @return
     */
    Object get(String key);

    /**
     * 设置有效期的key
     * @param key
     * @param expireTime
     * @param timeUnit
     * @return
     */
    boolean expire(String key, Long expireTime, TimeUnit timeUnit);

    /**
     * 如果不存在key则保存数据
     * @param key
     * @param value
     * @return
     */
    boolean setIfAbsent(String key, String value);

    boolean setIfAbsent(String key, String value, Long expireTime, TimeUnit timeUnit);

    /**
     * 删除key
     * @param key
     * @return
     */
    boolean delete(String key);

    /**
     * 批量删除key
     * @param pattern
     */
    void batchDel(String... pattern);


    /**
     * 判断是否存在key
     *
     * @param key key
     * @return 返回
     */
    boolean hasKey(String key);

    void del(String... key);

    Long rightPush(String key, Object value);

    boolean set(String key, Object value);

    String getStr(String key);

    Long getListSize(String key);

    Object leftPop(String key);
}
