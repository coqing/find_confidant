package top.coqing.services.chat.service;

import top.coqing.services.model.chat.Chat;
import com.baomidou.mybatisplus.extension.service.IService;
import top.coqing.services.model.chat.response.ChatUser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
* @author coqing
* @description 针对表【chat】的数据库操作Service
* @createDate 2022-09-17 12:13:53
*/
public interface ChatService extends IService<Chat> {

    /**
     * 获取当前用户的聊天列表
     * @param userId
     */
    HashMap<String, Object> userList(long userId);

    /**
     * 发送消息
     * @param chat
     * @return
     */
    boolean send(Chat chat);

    /**
     * 获取两人间的聊天列表
     * @param userId
     * @param id
     * @return
     */
    List<Chat> chatList(Long userId, Long id);
}
