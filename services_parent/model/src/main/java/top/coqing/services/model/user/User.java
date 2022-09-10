package top.coqing.services.model.user;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;

import lombok.Data;
import top.coqing.services.model.base.BaseEntity;
import top.coqing.services.model.tag.Tag;

import java.util.List;

/**
 * 
 * @TableName user
 */
@TableName(value ="user")
@Data
public class User extends BaseEntity {

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

    /**
     * 用户昵称
     */
    private String userName;

    /**
     * 账号
     */
    private String userAccount;

    /**
     * 用户头像
     */
    private String avatarUrl;

    /**
     * 性别 0女 1男

     */
    private Integer gender;

    /**
     * 密码
     */
    private String password;

    /**
     * 电话
     */
    private String phone;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 状态   0正常  1冻结

     */
    private Integer status;



    /**
     * 简介
     */
    private String profile;

    /**
     * 用户权限    0
-普通用户    1-管理员
     */
    private Integer role;

    /**
     * 用户的标签列表
     */
    @TableField(exist = false)
    private List<Tag>  tagList;

    /**
     * 匹配度
     */
    @TableField(exist = false)
    private Double matchingDegree;


    @Override
    public String toString() {
        return "User{" +
                "userName='" + userName + '\'' +
                ", userAccount='" + userAccount + '\'' +
                ", avatarUrl='" + avatarUrl + '\'' +
                ", gender=" + gender +
                ", password='" + password + '\'' +
                ", phone='" + phone + '\'' +
                ", createTime='" + getCreateTime() + '\'' +
                ", updateTime='" + getUpdateTime() + '\'' +
                ", id='" + getId() + '\'' +
                ", isDeleted='" + getIsDeleted() + '\'' +
                ", email='" + email + '\'' +
                ", status=" + status +
                ", profile='" + profile + '\'' +
                ", role=" + role +
                ", tagList=" + tagList +
                '}';
    }
}