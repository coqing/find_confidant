package top.coqing.services.common.result;


import lombok.Getter;

/**
 * @Description: 统一返回结果码以及对应描述
 * @Author: coqing
 * @Date: 2022/9/2 17:21
 **/
@Getter
public enum StateCode {
    FAIL(0, "失败", ""),
    SUCCESS(1, "成功", ""),
    BAD_REQUEST(40000,"非法请求","非法请求"),
    NULL_ERROR(40100, "为空", ""),
    PARAMS_ERROR(40101, "参数错误", ""),
    LOGIN_AUTH(40200, "未登录", ""),
    PERMISSION(40301, "无权限", ""),
    SERVICE_ERROR(50000, "服务异常", "");


    private final int code;

    /**
     * 状态码信息
     */
    private final String message;

    /**
     * 详情描述
     */
    private final String description;

    StateCode(int code, String message, String description) {
        this.code = code;
        this.message = message;
        this.description = description;
    }

}
