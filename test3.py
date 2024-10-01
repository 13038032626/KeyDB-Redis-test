import redis
from datetime import datetime
from concurrent.futures import ThreadPoolExecutor


redis_pool = []
for i in range(16):
    redis_pool.append(redis.Redis(host='192.168.56.10', port=6379, db=0))


def insert_data(start, end,index):
    # r = redis.Redis(host='192.168.56.10', port=6378, db=0)
    r = redis_pool[index]
    for i in range(start, end):
        # key = f"key_{i}"
        # value = f"value_{i}"
        # r.set(key, value)
        r.get("testKey")


# 线程数
num_threads = 16
num_items = 100000
items_per_thread = num_items // num_threads

start_time = datetime.now()

with ThreadPoolExecutor(max_workers=num_threads) as executor:
    futures = []
    for i in range(num_threads):
        start_index = i * items_per_thread
        end_index = start_index + items_per_thread
        futures.append(executor.submit(insert_data, start_index, end_index, i))

# 等待所有线程完成
for future in futures:
    future.result()

end_time = datetime.now()
print(f"插入完毕 {end_time - start_time}")
