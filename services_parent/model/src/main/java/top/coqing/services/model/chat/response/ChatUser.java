package top.coqing.services.model.chat.response;


import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * @Description: 聊天列表的用户
 * @Author: coqing
 * @Date: 2022/9/17 12:54
 **/
@Data
public class ChatUser {


    private Long id;

    /**
     * 用户头像
     */
    private String avatarUrl;

    /**
     * 用户昵称
     */
    private String userName;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    /**
     * 消息内容
     */
    private String text;

    /**
     * 0未读 1已读
     */
    private Integer isReaded;
}
