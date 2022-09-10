package top.coqing.services.model.user.request;

import lombok.Data;

import java.io.Serializable;

@Data
public class LoginUser implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 账号
     */
    private String userAccount;



    /**
     * 密码
     */
    private String password;
}
