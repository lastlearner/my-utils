package com.lyling.util.constant.DistributedLockUtil.zookeeper;

/**
 * @ProjectName: my-utils
 * @Package: com.lyling.util.constant.DistributedLockUtil.zookeeper
 * @ClassName: ZookeeperToolTest
 * @Author: liaoyilang
 * @Description: ${description}
 * @Date: 2019/3/25 下午4:37
 * @Version: 1.0
 */
public class ZookeeperToolTest {
    static int n = 500;

    public static void secskill() {
        System.out.println(--n);
    }

    public static void main(String[] args) {


        Runnable runnable = new Runnable() {
            public void run() {
                ZookeeperTool lock = null;
                try {
                    lock = new ZookeeperTool("127.0.0.1:2181", "test1");
                    lock.lock();
                    secskill();
                    System.out.println(Thread.currentThread().getName() + "正在运行");
                } finally {
                    if (lock != null) {
                        lock.unlock();
                    }
                }
            }
        };

        for (int i = 0; i < 10; i++) {
            Thread t = new Thread(runnable);
            t.start();
        }
    }
}
