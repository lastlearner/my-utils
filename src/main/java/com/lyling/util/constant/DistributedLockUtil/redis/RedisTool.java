package com.lyling.util.constant.DistributedLockUtil.redis;

import redis.clients.jedis.Jedis;

import java.util.Collections;

/**
 * @ProjectName: my-utils
 * @Package: com.lyling.util.constant.DistributedLockUtil.redis
 * @ClassName: RedisTool
 * @Author: liaoyilang
 * @Description: ${description}
 * @Date: 2019/3/25 下午4:01
 * @Version: 1.0
 */
public class RedisTool {


        private static final String LOCK_SUCCESS = "OK";
        private static final String SET_IF_NOT_EXIST = "NX";
        private static final String SET_WITH_EXPIRE_TIME = "PX";

        /**
         * 尝试获取分布式锁
         * @param jedis Redis客户端
         * @param lockKey 锁
         * @param requestId 请求标识
         * @param expireTime 超期时间
         * @return 是否获取成功
         */
        public static boolean tryGetDistributedLock(Jedis jedis, String lockKey, String requestId, int expireTime) {

            String result = jedis.set(lockKey, requestId, SET_IF_NOT_EXIST, SET_WITH_EXPIRE_TIME, expireTime);

            if (LOCK_SUCCESS.equals(result)) {
                return true;
            }
            return false;

        }


    private static final Long RELEASE_SUCCESS = 1L;

    /**
     * 释放分布式锁
     * @param jedis Redis客户端
     * @param lockKey 锁
     * @param requestId 请求标识
     * @return 是否释放成功
     */
    public static boolean releaseDistributedLock(Jedis jedis, String lockKey, String requestId) {

        //lua脚本，保证原子性
        //首先获取锁对应的value值，检查是否与requestId相等，如果相等则删除锁（解锁）。
        String script = "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";
        Object result = jedis.eval(script, Collections.singletonList(lockKey), Collections.singletonList(requestId));

        if (RELEASE_SUCCESS.equals(result)) {
            return true;
        }
        return false;

    }

}
