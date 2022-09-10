package top.coqing.services.user.controller;

import com.baomidou.mybatisplus.extension.api.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import top.coqing.services.common.exception.GlobalException;
import top.coqing.services.common.result.Result;
import top.coqing.services.common.result.StateCode;
import top.coqing.services.model.user.User;
import top.coqing.services.user.service.UserService;

import javax.annotation.Resource;
import java.util.List;

/**
 * @Description: 需要登录才能请求的用户接口
 * @Author: coqing
 * @Date: 2022/9/5 17:56
 **/
@RestController
@RequestMapping("/api/user/auth")
@Slf4j
public class UserAuthController {

    @Resource
    private UserService userService;

    /**
     * 修改用户信息
     * @param updateUser
     * @param userId
     * @return
     */
    @PostMapping("/update")
    public Result update(@RequestBody User updateUser,@RequestHeader("userId") long userId){

        if(updateUser==null){
            throw new GlobalException(StateCode.PARAMS_ERROR);
        }
        if(updateUser.getId()!=userId){
            throw new GlobalException(StateCode.BAD_REQUEST);
        }
        User user = userService.updateUser(updateUser);

        if(user==null){
            return Result.fail();
        }
        return Result.success(user);
    }

    /**
     * 根据用户自身的标签匹配知己
     * @param userId
     * @return
     */
    @GetMapping("/matching")
    public Result matching(@RequestHeader("userId") long userId){
        List<User> confidants = userService.matching(userId);
        return Result.success(confidants);
    }

    /**
     * 根据网关操作token获取的userId查询用户信息
     * @param userId
     * @return
     */
    @GetMapping("/info")
    public Result info(@RequestHeader("userId") long userId){
        User oneUserAndTags = userService.getOneUserAndTags(userId);
        return Result.success(oneUserAndTags);
    }



}
