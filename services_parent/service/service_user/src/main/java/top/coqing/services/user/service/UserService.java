package top.coqing.services.user.service;

import top.coqing.services.model.tag.Tag;
import top.coqing.services.model.user.User;
import com.baomidou.mybatisplus.extension.service.IService;
import top.coqing.services.model.user.request.LoginUser;

import java.util.List;

/**
* @author coqing
* @description 针对表【user】的数据库操作Service
* @createDate 2022-09-02 23:20:29
*/
public interface UserService extends IService<User> {

    /*
    查询脱敏后的全部用户
     */
    List<User> findAllUser();

    /*
    用户注册 至少包含账号和密码
     */
    boolean register(User user);

    /**
     * 用户登录
     * @param loginUser
     * @return 敏后的user
     */
    User login(LoginUser loginUser);

    /**
     * 获取用户信息
     * @param id
     * @return
     */
    User getOneUserAndTags(long id);


    /**
     * 初始化用户和标签到redis
     * @return
     */
    boolean initUserAndTagsToRedis();

    /*
    根据用户自身的标签匹配知己
     */
    List<User> matching(long userId);

    /**
     * 修改用户信息
     * @param updateUser
     * @return
     */
    User updateUser(User updateUser);

    /**
     * 更新缓存中的用户标签
     * @param tags
     * @param id
     * @return
     */
    User updateUserTags(List<Tag> tags, Long id);

    /**
     * 关注或取消关注
     * @param userId
     * @param id
     * @return
     */
    boolean follow(Long userId, Long id);

    /**
     * 获取关注列表
     * @param userId
     * @return
     */
    List<User> followList(Long userId);

    /**
     * 判断是否关注对方
     * @param userId
     * @param id
     * @return
     */
    boolean isFollow(Long userId, Long id);
}
