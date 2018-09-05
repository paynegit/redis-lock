package com.aaxis.redis.redislock.controller;

import com.aaxis.redis.redislock.util.AquiredLockWorker;
import com.aaxis.redis.redislock.util.RedisLocker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author paynejia
 * This class aims at testing redis distrubited lock with multiple thread to minic multiple clients
 */
@RestController
public class RedisLockController {

    protected static final Logger logger = LoggerFactory.getLogger(RedisLockController.class);
    @Autowired
    RedisLocker distributedLocker;

    @RequestMapping(value = "/testlock")
    public String testRedlock() throws Exception{

        CountDownLatch startSignal = new CountDownLatch(1);
        CountDownLatch doneSignal = new CountDownLatch(5);
        ExecutorService exec = Executors.newFixedThreadPool(5);
        for (int i = 0; i < 5; ++i) { // create and start threads
            //new Thread(new Worker(startSignal, doneSignal)).start();
            exec.submit(new Worker(startSignal, doneSignal));
        }
        exec.shutdown();
        startSignal.countDown(); // let all threads proceed
        doneSignal.await();
        logger.info("All processors done. Shutdown connection");
        return "redlock";
    }

    /**
     * Thread to mimic multiple clients.
     */
    class Worker implements Runnable {
        private final CountDownLatch startSignal;
        private final CountDownLatch doneSignal;

        Worker(CountDownLatch startSignal, CountDownLatch doneSignal) {
            this.startSignal = startSignal;
            this.doneSignal = doneSignal;
        }

        public void run() {
            try {
                startSignal.await();
                distributedLocker.lock("test",new AquiredLockWorker<Object>() {

                    @Override
                    public Object invokeAfterLockAquire() {
                        doTask();
                        return null;
                    }

                });
            }catch (Exception e){

            }
        }

        void doTask() {
            logger.info(Thread.currentThread().getName() + " start");
            Random random = new Random();
            int _int = random.nextInt(200);
            logger.info(Thread.currentThread().getName() + " sleep " + _int + "millis");
            try {
                Thread.sleep(_int);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            logger.info(Thread.currentThread().getName() + " end");
            doneSignal.countDown();
        }
    }
}
