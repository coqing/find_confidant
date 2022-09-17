package top.coqing.services.model.chat;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;
import top.coqing.services.model.base.BaseEntity;

/**
 * 
 * @TableName chat
 */
@TableName(value ="chat")
@Data
public class Chat extends BaseEntity {

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

    /**
     * 发送者id
     */
    private Long fromId;

    /**
     * 接收者id
     */
    private Long toId;

    /**
     * 消息内容
     */
    private String text;

    /**
     * 0未读 1已读
     */
    private Integer isReaded;




}