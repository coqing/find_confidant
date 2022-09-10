package top.coqing.services.user.controller;



import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import top.coqing.services.common.result.Result;
import top.coqing.services.model.tag.Tag;
import top.coqing.services.model.user.User;
import top.coqing.services.user.service.UserService;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/admin/user")
@Slf4j
public class UserAdminController {


    @Resource
    private UserService userService;

    /*
    查询脱敏后的全部用户
     */
    @GetMapping("/findAllUser")
    public Result findAllUser(){
        List<User> userList = userService.findAllUser();
        return Result.success(userList);
    }

    /**
     * 更新缓存中的用户标签
     * @param tags
     * @param id
     */
    @PostMapping("/updateUserTags/{id}")
    public boolean updateUserTags(@RequestBody List<Tag> tags, @PathVariable("id") Long id){
        userService.updateUserTags(tags,id);
        return true;
    }

}
