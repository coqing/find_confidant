package top.coqing.services.chat.controller;


import com.sun.org.apache.xalan.internal.xsltc.cmdline.getopt.GetOptsException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;
import top.coqing.services.chat.service.ChatService;
import top.coqing.services.common.exception.GlobalException;
import top.coqing.services.common.result.Result;
import top.coqing.services.common.result.StateCode;
import top.coqing.services.model.chat.Chat;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;


/**
 * @Description: 需要登录才能请求的聊天消息接口
 * @Author: coqing
 * @Date: 2022/9/17 12:23
 **/
@RestController
@RequestMapping("/api/chat/auth")
@Slf4j
public class ChatAuthController {


    @Resource
    private ChatService chatService;

    /**
     * 获取两人间的聊天列表
     * @param userId
     * @param id
     * @return
     */
    @GetMapping("/chatList/{id}")
    public Result chatList(@RequestHeader("userId") Long userId,@PathVariable("id") Long id){
        List<Chat> chatList = chatService.chatList(userId,id);
        if(chatList==null){
            return Result.fail();
        }
        return Result.success(chatList);
    }

    /**
     * 发送消息
     * @param chat
     * @return
     */
    @PostMapping("/send")
    public Result send(@RequestHeader("userId") long userId,@RequestBody Chat chat){

        //验证chat信息
        if(chat==null){
            throw new GlobalException(StateCode.NULL_ERROR);
        }
        // null 空字符串 空格
        if(StringUtils.isAnyBlank(chat.getText())){
            throw new GlobalException(StateCode.PARAMS_ERROR);
        }
        if(chat.getToId()==null||chat.getToId()<=0){
            throw new GlobalException(StateCode.PARAMS_ERROR);
        }
        chat.setFromId(userId);
        boolean flag = chatService.send(chat);
        if(flag){
            return Result.success();
        }
        return Result.fail();
    }

    /**
     * 获取当前用户的聊天列表
     * @param userId
     * @return
     */
    @GetMapping("/userList")
    public Result userList(@RequestHeader("userId") long userId){
        HashMap<String, Object> res = chatService.userList(userId);

        return Result.success(res);
    }
}
