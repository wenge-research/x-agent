package com.wg.appframe.core.bean;

import lombok.Data;

@Data
public class RedisLockDefinitionHolder {

    /**
     * 业务唯一 key
     */
    private String businessKey;

    /**
     * 加锁时间 (秒 s)
     */
    private Long lockTime;

    /**
     * 上次更新时间（ms）
     */
    private Long lastModifyTime;

    /**
     * 保存当前线程
     */
    private Thread currentTread;
    /**
     * 当前尝试次数
     */
    private int currentCount;

    /**
     * 更新的时间周期（毫秒）,公式 = 加锁时间（转成毫秒） / 3
     */
    private Long modifyPeriod;

    public RedisLockDefinitionHolder(String businessKey, Long lockTime, Long lastModifyTime, Thread currentTread) {
        this.businessKey = businessKey;
        this.lockTime = lockTime;
        this.lastModifyTime = lastModifyTime;
        this.currentTread = currentTread;
        this.modifyPeriod = lockTime * 1000 / 3;
    }
}
