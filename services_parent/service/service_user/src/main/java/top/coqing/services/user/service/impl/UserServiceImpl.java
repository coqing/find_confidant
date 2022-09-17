package top.coqing.services.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.swagger.models.auth.In;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.DigestUtils;
import top.coqing.services.common.exception.GlobalException;
import top.coqing.services.common.result.StateCode;
import top.coqing.services.model.tag.Tag;
import top.coqing.services.model.user.Follow;
import top.coqing.services.model.user.User;
import top.coqing.services.model.user.request.LoginUser;
import top.coqing.services.tag.client.TagFeignClient;
import top.coqing.services.user.mapper.FollowMapper;
import top.coqing.services.user.service.UserService;
import top.coqing.services.user.mapper.UserMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static top.coqing.services.common.constant.RedisKeyConstant.KEY_USER_AND_TAGS;
import static top.coqing.services.common.constant.RedisKeyConstant.KEY_USER_FOLLOW;

/**
* @author coqing
* @description 针对表【user】的数据库操作Service实现
* @createDate 2022-09-02 23:20:29
*/
@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
    implements UserService{

    @Resource
    private UserMapper userMapper;

    @Resource
    private TagFeignClient tagFeignClient;

    @Resource
    private RedisTemplate redisTemplate;

    @Resource
    private FollowMapper followMapper;


    private static final String SALT = "coqing-aabbccdd";

    /*
    查询脱敏后的全部用户
     */
    @Override
    public List<User> findAllUser() {
        QueryWrapper<User> userQueryWrapper = new QueryWrapper<>();
        List<User> users = baseMapper.selectList(userQueryWrapper)
                                    .stream().map(user -> getSafetyUser(user)).collect(Collectors.toList());

        return users;
    }

    /*
    用户注册 至少包含账号和密码
     */
    @Override
    public boolean register(User user) {
        if(user.getUserAccount().length() < 4) {
            throw new GlobalException(StateCode.PARAMS_ERROR, "账号过短");
        }

        if(user.getPassword().length() < 8) {
            throw new GlobalException(StateCode.PARAMS_ERROR, "密码过短");
        }

        String validPattern = "[`~!@#$%^&*()+=|{}':;',\\\\[\\\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]";
        Matcher matcher = Pattern.compile(validPattern).matcher(user.getUserAccount());
        if (matcher.find()) {
            throw new GlobalException(StateCode.PARAMS_ERROR, "账户不能包含特殊字符");
        }

        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_account", user.getUserAccount());
        long count = userMapper.selectCount(queryWrapper);
        if (count > 0) {
            throw new GlobalException(StateCode.PARAMS_ERROR, "账号重复");
        }

        User newUser = new User();
        newUser.setUserAccount(user.getUserAccount());
        newUser.setUserName(user.getUserAccount());
        newUser.setPassword(DigestUtils.md5DigestAsHex((SALT + user.getPassword()).getBytes()));
        if(null!=user.getGender()){
            if(user.getGender()!=0){
                newUser.setGender(1);
                newUser.setAvatarUrl("https://coqing.oss-cn-shenzhen.aliyuncs.com/avatar/default/man.jpg");
            }
        }
        if(null!=user.getAvatarUrl()){
            newUser.setAvatarUrl(user.getAvatarUrl());
        }

        boolean res = this.save(newUser);
        if (!res) {
            return false;
        }
        return true;
    }

    /**
     * 用户登录
     * @param loginUser
     * @return 敏后的user
     */
    @Override
    public User login(LoginUser loginUser) {
        if(loginUser.getUserAccount().length() < 4) {
            throw new GlobalException(StateCode.PARAMS_ERROR, "账号过短");
        }

        if(loginUser.getPassword().length() < 8) {
            throw new GlobalException(StateCode.PARAMS_ERROR, "密码过短");
        }

        String validPattern = "[`~!@#$%^&*()+=|{}':;',\\\\[\\\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]";
        Matcher matcher = Pattern.compile(validPattern).matcher(loginUser.getUserAccount());
        if (matcher.find()) {
            throw new GlobalException(StateCode.PARAMS_ERROR, "账户不能包含特殊字符");
        }

        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_account", loginUser.getUserAccount());
        queryWrapper.eq("password", DigestUtils.md5DigestAsHex((SALT + loginUser.getPassword()).getBytes()));

        User user = userMapper.selectOne(queryWrapper);

        return getSafetyUser(user);
    }

    /**
     * 获取用户信息
     * @param id
     * @return
     */
    @Override
    public User getOneUserAndTags(long id) {
        String key = KEY_USER_AND_TAGS+"::"+id;
        User res = (User) redisTemplate.opsForValue().get(key);
        if(res==null){
            System.out.println(id);
            System.out.println("666");
            res = userMapper.selectById(id);
            if (res==null){
                throw new GlobalException(StateCode.FAIL,"没有该用户");
            }
            res.setTagList(tagFeignClient.tagsById(res.getId()));
            redisTemplate.opsForValue().set(key,res);
        }

        return getSafetyUser(res);
    }

    /**
     * 初始化用户和标签到redis
     * @return
     */
    @Override
    public boolean initUserAndTagsToRedis() {
        Set keys = redisTemplate.keys(KEY_USER_AND_TAGS + "*");
        Long delete = redisTemplate.delete(keys);
        log.info(String.valueOf(delete));

        QueryWrapper<User> userQueryWrapper = new QueryWrapper<>();
        //TODO 三项字符串拼接 改成 两项
        String key = KEY_USER_AND_TAGS+"::";
        userMapper.selectList(userQueryWrapper).forEach(user -> {
            user.setTagList(tagFeignClient.tagsById(user.getId()));
            System.out.println(user.getId()+"------------------");
            getSafetyOriginUser(user);
            redisTemplate.opsForValue().set(key+user.getId(),user);
        });

        return true;
    }

    /**
     * 根据用户自身的标签匹配知己
     * @param userId
     * @return
     */
    @Override
    public List<User> matching(long userId) {
        User user = getOneUserAndTags(userId);
        List<Tag> tagList = user.getTagList();
        // 用户未设置标签
        if (null==tagList||tagList.size()==0){
            return null;
        }

        Set keys = redisTemplate.keys(KEY_USER_AND_TAGS + "*");
        List<User> userList = redisTemplate.opsForValue().multiGet(keys);
        List<User> resList = new ArrayList<>();

        List<Tag> longer, shorter;
        for (User otherUser : userList){
            List<Tag> otherTagList = otherUser.getTagList();
            if(user.getId()==otherUser.getId()){
                continue;
            }
            if (null==tagList||tagList.size()==0){
                otherUser.setMatchingDegree(0.0);
                continue;
            }

            if (tagList.size()>=otherTagList.size()){
                longer=tagList;
                shorter=otherTagList;
            }else{
                longer=otherTagList;
                shorter=tagList;
            }

            double maxRate = 1.0/longer.size();
            double minRate = maxRate/shorter.size();
            double res=0.0;
            for(Tag longerTag : longer){
                boolean flag = false;
                double temp = 0.0;
                for(Tag shorterTag : shorter){
                    if (longerTag.getId()==shorterTag.getId()){
                        flag=true;
                        break;
                    }

                    if(longerTag.getParent()==shorterTag.getParent()){
                        temp+=minRate;
                    }
                }
                if(flag){
                    res+=maxRate;
                }else {
                    res+= temp/2;
                }
            }
            otherUser.setMatchingDegree(res);
            resList.add(otherUser);
        }

        Collections.sort(resList, new Comparator<User>() {
            @Override
            public int compare(User o1, User o2) {
                return o1.getMatchingDegree()>=o2.getMatchingDegree()?-1:1;
            }
        });

//        for (User t : userList){
//            System.out.println(t);
//        }

        return resList;
    }

    /**
     * 修改用户信息
     * @param updateUser
     * @return
     */
    @CachePut(value = KEY_USER_AND_TAGS,key = "#updateUser.id")
    @Override
    public User updateUser(User updateUser) {
//        User user = userMapper.selectById(updateUser.getId());
        User user = new User();
        user.setId(updateUser.getId());
        if(updateUser.getUserName()!=null){
            user.setUserName(updateUser.getUserName());
        }
        if(updateUser.getAvatarUrl()!=null){
            user.setAvatarUrl(updateUser.getAvatarUrl());
        }
        if(updateUser.getGender()!=null){
            user.setGender(updateUser.getGender());
        }
        if(updateUser.getProfile()!=null){
            user.setProfile(updateUser.getProfile());
        }

        int i = userMapper.updateById(user);
        if(i==0){
            return null;
        }

        user = userMapper.selectById(user.getId());
        User oneUserAndTags = getOneUserAndTags(user.getId());
        User safetyUser = getSafetyUser(user);
        safetyUser.setTagList(oneUserAndTags.getTagList());

        return safetyUser;
    }

    /**
     * 更新缓存中的用户标签
     * @param tags
     * @param id
     * @return
     */
    @Override
    @CachePut(value = KEY_USER_AND_TAGS,key = "#id")
    public User updateUserTags(List<Tag> tags, Long id) {
        User oneUserAndTags = getOneUserAndTags(id);
        oneUserAndTags.setTagList(tags);
        return oneUserAndTags;
    }

    /**
     * 关注或取消关注
     * @param userId
     * @param id
     * @return
     */
    @Override
    public boolean follow(Long userId, Long id) {
        String key = KEY_USER_FOLLOW+"::"+userId;
        QueryWrapper<Follow> followQueryWrapper = new QueryWrapper<>();
        followQueryWrapper.eq("from_id",userId);
        followQueryWrapper.eq("to_id",id);
        Integer integer = followMapper.selectCount(followQueryWrapper);
        if(integer>0){
            followMapper.delete(followQueryWrapper);
            redisTemplate.opsForSet().remove(key,id);
        }else{
            Follow follow = new Follow();
            follow.setFromId(userId);
            follow.setToId(id);
            followMapper.insert(follow);
            redisTemplate.opsForSet().add(key,id);
        }

        return true;
    }

    /**
     * 获取关注列表
     * @param userId
     * @return
     */
    @Override
    public List<User> followList(Long userId) {

        String key = KEY_USER_FOLLOW+"::"+userId;
        Set<Integer> sets = redisTemplate.opsForSet().members(key);

        if(sets.size()==0){
            QueryWrapper<Follow> followQueryWrapper = new QueryWrapper<>();
            followQueryWrapper.eq("from_id",userId);
            List<Follow> follows = followMapper.selectList(followQueryWrapper);
            for(Follow follow:follows){
                sets.add(follow.getToId().intValue());
                redisTemplate.opsForSet().add(key,follow.getToId());
            }
        }

        ArrayList<User> users = new ArrayList<>();
        for (Integer member : sets) {
            users.add(getOneUserAndTags((long)member));
        }

        return users;
    }

    /**
     * 判断是否关注对方
     * @param userId
     * @param id
     * @return
     */
    @Override
    public boolean isFollow(Long userId, Long id) {

        String key = KEY_USER_FOLLOW+"::"+userId;
        Boolean member = redisTemplate.opsForSet().isMember(key, id);
        return member;
    }


    /**
     * 用户数据脱敏
     * @param originUser
     * @return
     */
    public User getSafetyUser(User originUser)  {
        if (originUser == null) {
            return null;
        }
        User safetyUser = new User();
        safetyUser.setId(originUser.getId());
        safetyUser.setUserName(originUser.getUserName());
        safetyUser.setUserAccount(originUser.getUserAccount());
        safetyUser.setAvatarUrl(originUser.getAvatarUrl());
        safetyUser.setGender(originUser.getGender());
        safetyUser.setPhone(originUser.getPhone());
        safetyUser.setEmail(originUser.getEmail());
        safetyUser.setRole(originUser.getRole());
        safetyUser.setStatus(originUser.getStatus());
        safetyUser.setCreateTime(originUser.getCreateTime());
        safetyUser.setUpdateTime(originUser.getUpdateTime());
        safetyUser.setProfile(originUser.getProfile());
        safetyUser.setTagList(originUser.getTagList());
        return safetyUser;
    }

    /**
     * 用户数据脱敏,返回原user
     * @param originUser
     * @return
     */
    public void getSafetyOriginUser(User originUser)  {
        if (originUser == null) {
            return ;
        }
        originUser.setPassword(null);
        originUser.setIsDeleted(null);
        originUser.setParam(null);

    }
}




