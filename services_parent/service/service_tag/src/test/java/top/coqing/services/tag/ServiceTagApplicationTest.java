package top.coqing.services.tag;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import top.coqing.services.model.tag.Tag;
import top.coqing.services.model.user.User;
import top.coqing.services.tag.aspect.CallTime;
import top.coqing.services.tag.mapper.TagMapper;
import top.coqing.services.tag.service.TagService;

import javax.annotation.Resource;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import static top.coqing.services.common.constant.RedisKeyConstant.KEY_TAG;

@SpringBootTest
@Slf4j
class ServiceTagApplicationTest {

    @Resource
    private TagMapper tagMapper;

    @Resource
    private RedisTemplate redisTemplate;

    @Resource
    private TagService tagService;


    @Test
    void redis4(){
        String myKey="article_uv_"+1;
        System.out.println(redisTemplate.opsForSet().size("aaaa"));
        Boolean aBoolean = redisTemplate.hasKey(myKey);
        System.out.println(aBoolean);
        System.out.println(redisTemplate.opsForSet().add(myKey, 1));
        System.out.println(redisTemplate.opsForSet().add(myKey, 2));
        System.out.println(redisTemplate.opsForSet().add(myKey, 1));
        System.out.println(redisTemplate.opsForSet().size(myKey));
        System.out.println(redisTemplate.hasKey(myKey));

    }


    @Test
    void redis3(){
        redisTemplate.opsForZSet().incrementScore(KEY_TAG+"rank", 2, 2);
        redisTemplate.opsForZSet().incrementScore("ranking-list", "p2", 2);
        redisTemplate.opsForZSet().incrementScore("ranking-list", "p3", 2);
        redisTemplate.opsForZSet().incrementScore("ranking-list", "p4", 2);
        redisTemplate.opsForZSet().incrementScore("ranking-list", "p5", 2);
        redisTemplate.opsForZSet().incrementScore("ranking-list", "p6", 1);
        redisTemplate.opsForZSet().incrementScore("ranking-list", "p1", -1);
        Set<String> ranking = redisTemplate.opsForZSet().reverseRange("ranking-list", 0, 4);
        System.out.println(ranking);
        Set<ZSetOperations.TypedTuple<String>> tuples = redisTemplate.opsForZSet().reverseRangeWithScores("ranking-list", 0, 4);
        for (ZSetOperations.TypedTuple<String> tuple : tuples) {
            System.out.println(tuple.getValue() + " : " + tuple.getScore());
        }
    }

    @Test
    void redis23(){
        String nb = tagService.redis2("nb");
    }


    @Test
    void redis2(){
        String nb = tagService.redis1("tt");
        String cc = tagService.redis1("aa");
        Object o = redisTemplate.opsForValue().get("userAndTags::aa");
        System.out.println(o);
//        String nb1 = tagService.redis3("tt");
    }


    @Test
    void redis1(){
        User user = new User();
        ArrayList<Tag> tags = new ArrayList<>();
        Tag t = new Tag();
        t.setText("奶茶");
        tags.add(t);
        Tag t1 = new Tag();
        t1.setText("火锅");
        tags.add(t1);
        user.setUserName("nb");
        user.setTagList(tags);

        System.out.println(user);
        redisTemplate.opsForValue().set("user:4",user);

        User o = (User)redisTemplate.opsForValue().get("user:4");
        System.out.println(o);
    }

    @Test
    void test1(){
        QueryWrapper<Tag> tagQueryWrapper = new QueryWrapper<>();
        List<Tag> tags = tagMapper.selectList(tagQueryWrapper);
        tags.forEach(tag -> System.out.println(tag));
    }

    /*
    添加单个联系
    如何判断tag_id的合理性:是子标签，parentid不为0   存在：
     */
    @Test
    void test2(){
        Long userId = 1l , tagId = 2l;
        // 判断tagid在tag表存在且parentid不为0
        // tagid和userid在多对多表里查询这个关系存不存在
        int i = tagMapper.insertUserTagOne(userId, tagId);
        System.out.println(i);
    }

    /*
    删除单个联系
     */
    @Test
    void test3(){
        Long userId = 1l , tagId = 2l;
        int i = tagMapper.deleteUserTagOne(userId, tagId);
        System.out.println(i);
    }

    /*
    查询是否存在这个关系
     */
    @Test
    void test4(){
        Long userId = 1l;
        List<Tag> tags = tagMapper.selectTagList(userId);
        System.out.println(tags);
        System.out.println(tagMapper.selectTagList(2l));
    }

    /*
    根据userId查询该用户的所有标签
     */
    @Test
    void test5(){
        Long userId = 1l , tagId = 2l;
        Integer count = tagMapper.selectUserTag(userId,tagId);
        System.out.println(count);
    }
}