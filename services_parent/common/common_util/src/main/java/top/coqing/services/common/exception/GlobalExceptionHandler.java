package top.coqing.services.common.exception;


import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import top.coqing.services.common.result.Result;
import top.coqing.services.common.result.StateCode;

/**
 * @Description: 全局异常处理器
 * @Author: coqing
 * @Date: 2022/9/2 18:31
 **/
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(GlobalException.class)
    public Result businessExceptionHandler(GlobalException e) {
        log.error("globalException: " + e.getMessage(), e);
        return new Result().code(e.getCode()).message(e.getMessage()).description(e.getDescription());
    }

    @ExceptionHandler(RuntimeException.class)
    public Result runtimeExceptionHandler(RuntimeException e) {
        log.error("runtimeException", e);
        return Result.build(StateCode.SERVICE_ERROR,e.getMessage());
    }
}
