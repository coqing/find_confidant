package top.coqing.services.user;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import top.coqing.services.model.tag.Tag;
import top.coqing.services.model.user.User;

import top.coqing.services.tag.client.TagFeignClient;
import top.coqing.services.user.mapper.UserMapper;
import top.coqing.services.user.service.UserService;

import javax.annotation.Resource;
import java.util.List;
import java.util.Set;

import static top.coqing.services.common.constant.RedisKeyConstant.KEY_USER_AND_TAGS;


@SpringBootTest
@Slf4j
class ServiceUserApplicationTest {

    @Resource
    private UserMapper userMapper;

    @Resource
    private TagFeignClient tagAdminFeignClient;

    @Resource
    private RedisTemplate redisTemplate;

    @Resource
    private UserService userService;

    @Test
    void deleteRedis(){

        User oneUserAndTags = userService.getOneUserAndTags(1l);
        System.out.println(oneUserAndTags);
//        Set keys = redisTemplate.keys(KEY_USER_AND_TAGS + "*");
//        Long delete = redisTemplate.delete(keys);
//        System.out.println(delete);
//        List<User> list = redisTemplate.opsForValue().multiGet(keys);
//        System.out.println(list);
    }

    @Test
    void feign(){
        List<Tag> tagList = tagAdminFeignClient.tagsById(10l);
        System.out.println(tagList);
    }

    @Test
    void test(){
        log.info("1");
        User user = new User();
        user.setUserName("coqing");
        user.setPassword("123");

        int insert = userMapper.insert(user);

        System.out.println(user.getId());
    }

    @Test
    void test1(){
        User user = userMapper.selectById(9);
        user.setUserName("ggg");
        System.out.println(user);
        userMapper.updateById(user);


        System.out.println(user);
    }
}