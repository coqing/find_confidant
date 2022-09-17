package top.coqing.services.chat.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import top.coqing.services.chat.mapper.ChatMapper;
import top.coqing.services.chat.service.ChatService;
import top.coqing.services.model.chat.Chat;
import org.springframework.stereotype.Service;
import top.coqing.services.model.chat.response.ChatUser;
import top.coqing.services.model.user.User;
import top.coqing.services.user.client.UserFeignClient;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

/**
* @author coqing
* @description 针对表【chat】的数据库操作Service实现
* @createDate 2022-09-17 12:13:53
*/
@Service
public class ChatServiceImpl extends ServiceImpl<ChatMapper, Chat>
    implements ChatService {

    @Resource
    private ChatMapper chatMapper;

    @Resource
    private UserFeignClient userFeignClient;


    /**
     * 获取当前用户的聊天列表
     * @param userId
     */
    @Override
    public HashMap<String, Object> userList(long userId) {
        List<Chat> chats = chatMapper.userList(userId, 20);

        ArrayList<ChatUser> chatUsers = new ArrayList<>();
        // 记录已进入列表的id
        HashSet<Long> set = new HashSet<>();

        boolean flag = false;

        for (Chat chat : chats) {
            if(userId==chat.getFromId()){ //我发的消息是最新的  全部置为 1:已读
                boolean contains = set.contains(chat.getToId());
                if(contains){
                    continue;
                }
                set.add(chat.getToId());
                ChatUser chatUser = buildChatUser(chat, chat.getToId());
                chatUser.setIsReaded(1);
                chatUsers.add(chatUser);
            }else{ //别人发的消息是最新的
                boolean contains = set.contains(chat.getFromId());
                if(contains){
                    continue;
                }
                set.add(chat.getFromId());
                ChatUser chatUser = buildChatUser(chat, chat.getFromId());
                if(!flag&&chatUser.getIsReaded().equals(0)){
                    flag=true;
                }
                chatUsers.add(chatUser);
            }
        }

        HashMap<String, Object> map = new HashMap<>();
        map.put("new",flag);
        map.put("chatUsers",chatUsers);
        return map;
    }

    /**
     * 发送消息
     * @param chat
     * @return
     */
    @Override
    public boolean send(Chat chat) {

        int insert = chatMapper.insert(chat);
        if(insert>0){
            return true;
        }
        return false;
    }

    /**
     * 获取两人间的聊天列表
     * @param userId
     * @param id
     * @return
     */
    @Override
    public List<Chat> chatList(Long userId, Long id) {

        QueryWrapper<Chat> chatQueryWrapper = new QueryWrapper<>();
        chatQueryWrapper.eq("from_id",userId);
        chatQueryWrapper.eq("to_id",id);
        chatQueryWrapper.or();
        chatQueryWrapper.eq("from_id",id);
        chatQueryWrapper.eq("to_id",userId);
        List<Chat> chats = chatMapper.selectList(chatQueryWrapper);

        return chats;
    }


    /**
     * 设置列表用户的信息
     * @param chat
     * @return
     */
    private ChatUser buildChatUser(Chat chat,Long id){
        ChatUser chatUser = new ChatUser();

        chatUser.setId(id);
        chatUser.setCreateTime(chat.getCreateTime());
        chatUser.setText(chat.getText());
        chatUser.setIsReaded(chat.getIsReaded());
        // 远程调用获取列表用户的信息
        User info = userFeignClient.info(id);
        chatUser.setUserName(info.getUserName());
        chatUser.setAvatarUrl(info.getAvatarUrl());

        return chatUser;
    }


}




