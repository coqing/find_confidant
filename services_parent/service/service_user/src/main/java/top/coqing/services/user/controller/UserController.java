package top.coqing.services.user.controller;



import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;
import top.coqing.services.common.JwtHelper.JwtHelper;
import top.coqing.services.common.exception.GlobalException;
import top.coqing.services.common.result.Result;
import top.coqing.services.common.result.StateCode;
import top.coqing.services.model.user.User;
import top.coqing.services.model.user.request.LoginUser;
import top.coqing.services.user.service.UserService;

import javax.annotation.Resource;
import java.util.HashMap;


/**
 * @Description: 不需要登录就能请求的用户接口
 * @Author: coqing
 * @Date: 2022/9/5 17:57
 **/
@RestController
@RequestMapping("/api/user")
@Slf4j
public class UserController {


    @Resource
    private UserService userService;

    /**
     * 用户登录
     * @param loginUser
     * @return 包含用户信息的token
     */
    @PostMapping("/login")
    public Result login(@RequestBody LoginUser loginUser){
        if(loginUser==null){
            throw new GlobalException(StateCode.NULL_ERROR);
        }
        // null 空字符串 空格
        if(StringUtils.isAnyBlank(loginUser.getUserAccount(),loginUser.getPassword())){
            throw new GlobalException(StateCode.PARAMS_ERROR);
        }

        User login = userService.login(loginUser);
        if(null==login){
            throw new GlobalException(StateCode.FAIL,"账号或密码错误");
        }

        HashMap<String, Object> loginResponse = new HashMap<>();

        // 获取包含标签列表的用户数据
        User userAndTags = userService.getOneUserAndTags(login.getId());

        loginResponse.put("token",JwtHelper.createToken(userAndTags));
        loginResponse.put("user",userAndTags);

        return Result.success(loginResponse);
    }

    /**
     * 用户注册 至少包含账号和密码
     * @param user
     * @return
     */
    @PostMapping("/register")
    public Result register(@RequestBody User user){
        if(user==null){
            throw new GlobalException(StateCode.NULL_ERROR);
        }
        // null 空字符串 空格
        if(StringUtils.isAnyBlank(user.getUserAccount(),user.getPassword())){
            throw new GlobalException(StateCode.PARAMS_ERROR);
        }

        boolean flag = userService.register(user);

        if(flag){
            return Result.success();
        }
        return Result.fail();
    }


    /**
     * 初始化用户和标签 TODO 后期移动到管理员权限下
     * @return
     */
    @GetMapping("/refresh")
    public Result refresh(){
        boolean res = userService.initUserAndTagsToRedis();
        if(res){
            return Result.success();
        }
        return Result.fail();
    }

}
