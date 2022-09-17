package top.coqing.services.model.user;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;
import top.coqing.services.model.base.BaseEntity;

/**
 * 用户关注的关系
 * @TableName follow
 */
@TableName(value ="follow")
@Data
public class Follow extends BaseEntity {

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

    /**
     * 用户id
     */
    private Long fromId;

    /**
     * 被关注者id
     */
    private Long toId;




}