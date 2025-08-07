//package com.wg.appframe.redis.lock;
//
//import com.wg.appframe.core.constant.GlobalConstant;
//import com.wg.appframe.core.exception.DistributedLockException;
//import com.wg.appframe.core.lock.DistributedLock;
//import com.wg.appframe.core.lock.ILock;
//import org.redisson.api.RLock;
//import org.redisson.api.RedissonClient;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
//import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
//
//import java.util.concurrent.TimeUnit;
//
///**
// * redisson分布式锁实现，基本锁功能的抽象实现
// * 本接口能满足绝大部分的需求，高级的锁功能，请自行扩展或直接使用原生api
// * https://gitbook.cn/gitchat/activity/5f02746f34b17609e14c7d5a
// *
// * @author zlt
// * @date 2020/5/5
// * <p>
// *
// *
// */
//@ConditionalOnClass(RedissonClient.class)
//@ConditionalOnProperty(prefix = "tbase.lock", name = "lockerType", havingValue = "redis")
//public class RedissonDistributedLock implements DistributedLock {
//
//    @Autowired(required = false)
//    private RedissonClient redissonClient;
//
//    private ILock getLock(String key, boolean isFair) {
//        RLock lock;
//        if (isFair) {
//            lock = redissonClient.getFairLock(GlobalConstant.Common.LOCK_KEY_PREFIX + ":" + key);
//        } else {
//            lock = redissonClient.getLock(GlobalConstant.Common.LOCK_KEY_PREFIX + ":" + key);
//        }
//        return new ILock(lock, this);
//    }
//
//    @Override
//    public ILock lock(String key, long leaseTime, TimeUnit unit, boolean isFair) {
//        ILock zLock = getLock(key, isFair);
//        RLock lock = (RLock) zLock.getLock();
//        lock.lock(leaseTime, unit);
//        return zLock;
//    }
//
//    @Override
//    public ILock tryLock(String key, long waitTime, long leaseTime, TimeUnit unit, boolean isFair) throws InterruptedException {
//        ILock zLock = getLock(key, isFair);
//        RLock lock = (RLock) zLock.getLock();
//        if (lock.tryLock(waitTime, leaseTime, unit)) {
//            return zLock;
//        }
//        return null;
//    }
//
//    @Override
//    public void unlock(Object lock) {
//        if (lock != null) {
//            if (lock instanceof RLock) {
//                RLock rLock = (RLock) lock;
//                if (rLock.isLocked()) {
//                    rLock.unlock();
//                }
//            } else {
//                throw new DistributedLockException("==>requires RLock type.");
//            }
//        }
//    }
//}
