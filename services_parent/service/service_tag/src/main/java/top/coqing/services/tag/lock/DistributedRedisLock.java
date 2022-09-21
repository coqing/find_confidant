package top.coqing.services.tag.lock;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

public class DistributedRedisLock implements Lock {

    private StringRedisTemplate stringRedisTemplate;
    private String lockName;
    private String uuid;
    private long expire = 30;

    public DistributedRedisLock(StringRedisTemplate stringRedisTemplate, String lockName, String uuid) {
        this.stringRedisTemplate = stringRedisTemplate;
        this.lockName = lockName;
        this.uuid = uuid;
    }

    @Override
    public void lock() {
        this.tryLock();
    }

    @Override
    public void lockInterruptibly() throws InterruptedException {

    }

    @Override
    public boolean tryLock() {
        try {
            return this.tryLock(-1L,TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean tryLock(long time, TimeUnit unit) throws InterruptedException {
        if(time!=-1){
            this.expire = unit.toSeconds(time);
        }

        String script = "if (redis.call('exists', KEYS[1]) == 0 or redis.call('hexists', KEYS[1], ARGV[1]) == 1) " +
                        "then " +
                        "   redis.call('hincrby', KEYS[1], ARGV[1], 1) " +
                        "   redis.call('expire', KEYS[1], ARGV[2]) " +
                        "   return 1 " +
                        "else " +
                        "   return 0 " +
                        "end";
        while(!this.stringRedisTemplate.execute(new DefaultRedisScript<>(script,
                Boolean.class), Arrays.asList(lockName), getId(), String.valueOf(expire))){
            Thread.sleep(50);
        }

        return true;
    }

    @Override
    public void unlock() {
        String script = "if (redis.call('hexists', KEYS[1], ARGV[1]) == 0) " +
                        "then " +
                        "   return nil " +
                        "elseif (redis.call('hincrby', KEYS[1], ARGV[1], -1) == 0) " +
                        "then " +
                        "  return redis.call('del',KEYS[1]) " +
                        "else " +
                        "  return 0 " +
                        "end";
        Long flag = this.stringRedisTemplate.execute(new DefaultRedisScript<>
                (script, Long.class), Arrays.asList(lockName), getId());
        if(flag==null){
            throw new IllegalMonitorStateException("this lock doesn't belong to you!");
        }

    }

    @Override
    public Condition newCondition() {
        return null;
    }

    String getId(){
        return uuid + ":" + Thread.currentThread().getId();
    }
}
