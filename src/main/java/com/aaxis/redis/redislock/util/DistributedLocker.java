package com.aaxis.redis.redislock.util;

/**
 * Created by paynejia on 2018/8/25.
 * the class to acquire lock.
 */
public interface DistributedLocker {

    /**
     * acquire lock
     * @param resourceName
     * @param worker
     * @param <T>
     * @return
     * @throws UnableToAquireLockException
     * @throws Exception
     */
    <T> T lock(String resourceName, AquiredLockWorker<T> worker) throws UnableToAquireLockException, Exception;

    <T> T lock(String resourceName, AquiredLockWorker<T> worker, int lockTime) throws UnableToAquireLockException, Exception;

}
