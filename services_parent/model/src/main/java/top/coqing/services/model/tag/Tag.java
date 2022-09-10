package top.coqing.services.model.tag;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

import lombok.Data;
import top.coqing.services.model.base.BaseEntity;

/**
 * 
 * @TableName tag
 */
@TableName(value ="tag")
@Data
public class Tag extends BaseEntity {

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;


    /**
     * 标签名称
     */
    private String text;

    /**
     * 用户 id
     */
    private Long createUser;

    /**
     * 父标签 id
     */
    private Long parent;


    /** TODO 设计模式
     * 子标签列表
     */
    @TableField(exist = false)
    private List<Tag> children;

    @Override
    public String toString() {
        return "Tag{" +
                "text='" + text + '\'' +
                ", createTime='" + getCreateTime() + '\'' +
                ", updateTime='" + getUpdateTime() + '\'' +
                ", id='" + getId() + '\'' +
                ", isDeleted='" + getIsDeleted() + '\'' +
                ", createUser=" + createUser +
                ", parent=" + parent +
                '}';
    }
}