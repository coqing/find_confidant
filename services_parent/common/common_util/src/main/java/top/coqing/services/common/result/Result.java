package top.coqing.services.common.result;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Description: 通用返回类
 * @Author: coqing
 * @Date: 2022/9/2 17:29
 **/
@Data
@ApiModel(value = "通用返回类")
public class Result<T> {

    @ApiModelProperty(value = "返回码")
    private int code;

    @ApiModelProperty(value = "返回数据")
    private T data;

    @ApiModelProperty(value = "返回消息")
    private String message;

    @ApiModelProperty(value = "返回详情描述")
    private String description;

    public Result(){}

    protected static <T> Result<T> build(T data) {
        Result<T> result = new Result<T>();
        if (data != null)
            result.setData(data);
        return result;
    }

    public Result<T> message(String msg){
        this.setMessage(msg);
        return this;
    }

    public Result<T> code(Integer code){
        this.setCode(code);
        return this;
    }

    public Result<T> description(String description){
        this.setDescription(description);
        return this;
    }


    /*
    数据，结果码
     */
    public static <T> Result<T> build(T body, StateCode stateCode) {
        Result<T> result = build(body);
        result.setCode(stateCode.getCode());
        result.setMessage(stateCode.getMessage());
        result.setDescription(stateCode.getDescription());
        return result;
    }

    /*
    结果码,详情描述
     */
    public static <T> Result<T> build(StateCode stateCode , String description) {
        Result<T> result =  build(null);
        result.setCode(stateCode.getCode());
        result.setMessage(stateCode.getMessage());
        result.setDescription(description);
        return result;
    }

    /*
    结果码
     */
    public static <T> Result<T> error( StateCode stateCode) {
        Result<T> result =  build(null);
        result.setCode(stateCode.getCode());
        result.setMessage(stateCode.getMessage());
        result.setDescription(stateCode.getDescription());
        return result;
    }



    /*
    成功：数据
     */
    public static<T> Result<T> success(T data){
        Result<T> result = build(data);
        return build(data, StateCode.SUCCESS);
    }

    /*
    成功：null
     */
    public static<T> Result<T> success(){
        return Result.success(null);
    }

    /*
    失败：数据
     */
    public static<T> Result<T> fail(T data){
        Result<T> result = build(data);
        return build(data, StateCode.FAIL);
    }

    /*
    失败：null
     */
    public static<T> Result<T> fail(){
        return Result.fail(null);
    }


}
