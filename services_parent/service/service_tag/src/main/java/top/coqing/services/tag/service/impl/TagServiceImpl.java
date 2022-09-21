package top.coqing.services.tag.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.swagger.models.auth.In;
import lombok.extern.log4j.Log4j;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.DefaultTypedTuple;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import top.coqing.services.model.tag.Tag;
import top.coqing.services.tag.lock.DistributedLockClient;
import top.coqing.services.tag.lock.DistributedRedisLock;
import top.coqing.services.tag.service.TagService;
import top.coqing.services.tag.mapper.TagMapper;
import org.springframework.stereotype.Service;
import top.coqing.services.user.client.UserFeignClient;

import javax.annotation.Resource;
import java.util.*;

import static top.coqing.services.common.constant.RedisKeyConstant.KEY_TAG;

/**
* @author coqing
* @description 针对表【tag】的数据库操作Service实现
* @createDate 2022-09-03 12:10:06
*/
@Slf4j
@Service
public class TagServiceImpl extends ServiceImpl<TagMapper, Tag>
    implements TagService{

    @Resource
    private TagMapper tagMapper;

    @Resource
    private UserFeignClient userFeignClient;

    @Resource
    private RedisTemplate redisTemplate;

    @Resource
    private DistributedLockClient distributedLockClient;

//    @Cacheable(value = "dict",keyGenerator = "keyGenerator")
    @Cacheable(value = "userAndTags",key = "#s")
    @Override
    public String redis1(String s) {
        System.out.println("nnn");
        return s;
    }

    @CacheEvict(value = "dict",allEntries = true)
    @Override
    public String redis2(String s) {
        return s;
    }

    @CachePut(value = "nb",key="#s")
    @Override
    public String redis3(String s) {
        System.out.println("666");
        return s+"666";
    }

    /*
    根据用户id查询tags
     */
    @Override
    public List<Tag> tagsById(Long id) {
        List<Tag> tagList = tagMapper.selectTagList(id);
        return tagList;
    }

    /**
     * 查询所有标签构成的标签树
     * @return
     */
    @Override
    public List<Tag> getTagTree() {

        // 分布式锁解决热度数据过期
        DistributedRedisLock redisLock = distributedLockClient.getRedisLock(KEY_TAG + "::distributed::tagTree");
        String key = KEY_TAG+"::tagTree";
        HashMap<Long, Tag> tagMap = (HashMap<Long, Tag>) redisTemplate.opsForValue().get(key);
        log.info("in");
        if(tagMap==null){
            redisLock.lock();
            log.info("lock");
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            try {
                tagMap = (HashMap<Long, Tag>) redisTemplate.opsForValue().get(key);
                if(tagMap==null){
                    log.info("select");
                    tagMap = new HashMap<>();
                    QueryWrapper<Tag> tagQueryWrapper = new QueryWrapper<>();
                    List<Tag> tagList = tagMapper.selectList(tagQueryWrapper);
                    if(tagList==null){
                        return null;
                    }else{
                        // 封装子父结构
                        for (Tag tag : tagList) {
                            if(tag.getParent()==0){
                                tag.setChildren(new ArrayList<>());
                                tagMap.put(tag.getId(),tag);
                                continue;
                            }
                            tagMap.get(tag.getParent()).getChildren().add(tag);
                        }
                        redisTemplate.opsForValue().setIfAbsent(key,tagMap);
                    }
                }
            }finally {
                redisLock.unlock();
            }

        }

        return new ArrayList<Tag>(tagMap.values());


//        // 双重检索
//        HashMap<Long, Tag> tagMap = (HashMap<Long, Tag>) redisTemplate.opsForValue().get(key);
//        if(tagMap==null){
//            synchronized (TagService.class){
//                tagMap = (HashMap<Long, Tag>) redisTemplate.opsForValue().get(key);
//                if(tagMap==null){
//                    tagMap = new HashMap<>();
//                    QueryWrapper<Tag> tagQueryWrapper = new QueryWrapper<>();
//                    List<Tag> tagList = tagMapper.selectList(tagQueryWrapper);
//                    if(tagList==null){
//                        return null;
//                    }else{
//                        // 封装子父结构
//                        for (Tag tag : tagList) {
//                            if(tag.getParent()==0){
//                                tag.setChildren(new ArrayList<>());
//                                tagMap.put(tag.getId(),tag);
//                                continue;
//                            }
//                            tagMap.get(tag.getParent()).getChildren().add(tag);
//                        }
//                        redisTemplate.opsForValue().setIfAbsent(key,tagMap);
//                    }
//                }
//            }
//        }
//
//        return new ArrayList<Tag>(tagMap.values());
    }

    /**
     * 更新用户标签列表
     * @param tags
     * @param userId
     * @return
     */
    @Override
    public boolean updateTags(List<Integer> tags, long userId) {
        List<Tag> tagList = tagMapper.selectTagList(userId);
        List<Tag> deleteList = new ArrayList<>();
        List<Tag> noDeleteList = new ArrayList<>();
        boolean flag;
        for(Tag oldTag : tagList){
            flag = false;
            for(Integer newTag : tags){
                if(oldTag.getId()==Long.valueOf(newTag)){
                    flag=true;
                    break;
                }
            }
            if (flag){ // 不更改
                noDeleteList.add(oldTag);
            }else{ //删除
                deleteList.add(oldTag);
            }
        }

        // 更新排行榜
        List<DefaultTypedTuple<Long>> updateTagRank = new ArrayList<>();

        for(Tag tag: deleteList){
            updateTagRank.add(new DefaultTypedTuple<Long>(tag.getId(),-1.0));
            tagMapper.deleteUserTagOne(userId,tag.getId());
        }
        for(Integer tag: tags){
            flag = true;
            for(Tag oldTag : noDeleteList){
                if(Long.valueOf(tag)==oldTag.getId()){
                    flag=false;
                    break;
                }
            }
            if(flag){
                updateTagRank.add(new DefaultTypedTuple<Long>(Long.valueOf(tag),1.0));
                tagMapper.insertUserTagOne(userId,Long.valueOf(tag));
            }
        }

        List<Tag> res = tagMapper.selectTagList(userId);
        userFeignClient.updateUserTags(res,userId);

        redisTemplate.opsForZSet().add(KEY_TAG+"rank",new HashSet<>(updateTagRank));

        return true;
    }

    /**
     * 获取标签热度排行
     * @return
     */
    @Override
    public Map<String, Integer> getTagRank() {

        Set<ZSetOperations.TypedTuple<Integer>> tuples =
                redisTemplate.opsForZSet().reverseRangeWithScores(KEY_TAG+"rank", 0, 4);

        List<Tag> tagTree = getTagTree();
        HashMap<String, Integer> getTagRank = new HashMap<>();
        //遍历排行榜
        for (ZSetOperations.TypedTuple<Integer> tuple : tuples) {
            long id = tuple.getValue();
            //遍历树的父标签
            for(Tag tagParent : tagTree){
                //遍历树的子标签
                for(Tag tag : tagParent.getChildren()){
                    if(tag.getId()==id){
                        //拿到标名称
                        getTagRank.put(tag.getText(),tuple.getScore().intValue());
                        break;
                    }
                }
            }
        }

        return getTagRank;
    }

    /**
     * 验证标签id是否合法
     * @param tags
     * @return
     */
    @Override
    public boolean isLegal(List<Integer> tags) {

        QueryWrapper<Tag> tagQueryWrapper = new QueryWrapper<>();
        List<Tag> tagList = tagMapper.selectList(tagQueryWrapper);

        if(tagList==null){
            return false;
        }

        boolean flag;
        for(Integer i:tags){
            flag = false; // 标签不存在的状态
            for(Tag tag: tagList){
                if(tag.getParent()!=0 && tag.getId().intValue() == i.intValue()){
                    //标签存在
                    flag=true;
                    break;
                }
            }
            if(!flag){
                return false;
            }
        }

        return true;
    }
}




