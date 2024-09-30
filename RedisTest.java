package KeyDB_Redis;

import redis.clients.jedis.Jedis;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class RedisTest {

    private static final String REDIS_HOST = "192.168.56.10";
    private static final int REDIS_PORT = 6379;
    private static final int NUM_THREADS = 16;
    private static final int NUM_ITEMS = 100000;

    public static void main(String[] args) {
        ExecutorService executor = Executors.newFixedThreadPool(NUM_THREADS);
        Instant startTime = Instant.now();

        int itemsPerThread = NUM_ITEMS / NUM_THREADS;

        for (int i = 0; i < NUM_THREADS; i++) {
            int startIndex = i * itemsPerThread;
            int endIndex = startIndex + itemsPerThread;
            int finalI = i;
            executor.submit(() -> insertData(startIndex, endIndex, finalI));
        }

        executor.shutdown();
        while (!executor.isTerminated()) {
            // Wait for all threads to finish
        }

        Instant endTime = Instant.now();
        System.out.println("插入完毕 " + Duration.between(startTime, endTime).toMillis() + " 毫秒");
    }

    static List<Jedis> jedisPool = new ArrayList<>();

    static {
        for (int i = 0; i < NUM_THREADS; i++) {
            jedisPool.add(new Jedis(REDIS_HOST, REDIS_PORT));
        }
    }

    private static void insertData(int start, int end, int index) {
        Jedis jedis = jedisPool.get(index);
        long startT = System.currentTimeMillis();
        for (int i = start; i < end; i++) {
            // String key = "key_" + i;
            // String value = "value_" + i;
            // jedis.set(key, value);
            jedis.get("testKey");
        }
        System.out.println("con");
        System.out.println(System.currentTimeMillis() - startT);
    }
}


