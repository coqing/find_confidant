package top.coqing.services.tag.lock;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.UUID;

@Component
public class DistributedLockClient {

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    private String uuid;

    public DistributedLockClient(){
        this.uuid = UUID.randomUUID().toString();
    }

    public DistributedRedisLock getRedisLock(String lockName){
        return new DistributedRedisLock(stringRedisTemplate,lockName,uuid);
    }
}
