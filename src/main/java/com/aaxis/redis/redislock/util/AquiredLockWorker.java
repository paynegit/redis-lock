package com.aaxis.redis.redislock.util;

public interface AquiredLockWorker<T> {
    /**
     *
     * @return
     * @throws Exception
     */
    T invokeAfterLockAquire() throws Exception;
}