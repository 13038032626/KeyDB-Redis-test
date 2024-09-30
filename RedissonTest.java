package KeyDB_Redis;

import org.redisson.Redisson;
import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class RedissonTest {
    private static final int NUM_THREADS = 16;
    private static final int NUM_ITEMS = 100000;
    static List<RedissonClient> redisPool = new ArrayList<>();

    static {
        Config config = new Config();
        config.useSingleServer().setAddress("redis://192.168.56.10:6378"); // Redis 地址
        for (int i = 0; i < 16; i++) {
            RedissonClient redisson = Redisson.create(config);
            redisPool.add(redisson);
        }
    }

    public static void main(String[] args) {
        ExecutorService executor = Executors.newFixedThreadPool(16);
        Instant startTime = Instant.now();

        int itemsPerThread = NUM_ITEMS / NUM_THREADS;

        for (int i = 0; i < NUM_THREADS; i++) {
            int startIndex = i * itemsPerThread;
            int endIndex = startIndex + itemsPerThread;
            int finalI = i;
            executor.submit(() -> insertData(startIndex, endIndex, finalI,System.currentTimeMillis()));
        }
    }

    public static void insertData(int start, int end, int index,long startTime) {
        RedissonClient redisson = redisPool.get(index);
        // 每个线程还是在一个redisson连接上，所以在线程上将请求异步，但是在连接层面还是被串行的，所以此时只能得知全部发出去大概要多长时间，但QPS受限于连接数
        for (int i = start; i < end; i++) {
            RBucket<String> bucket = redisson.getBucket("testKey");
            int finalI = i;
            bucket.getAsync().thenAccept(value -> {
                System.out.print(finalI);
            }).exceptionally(e -> null);
        }
        System.out.println("end  " + index + "  " + (System.currentTimeMillis() - startTime));
    }
}
