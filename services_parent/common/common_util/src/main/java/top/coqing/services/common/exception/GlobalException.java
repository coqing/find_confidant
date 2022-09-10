package top.coqing.services.common.exception;

import lombok.Getter;
import top.coqing.services.common.result.StateCode;

@Getter
public class GlobalException extends RuntimeException{

    private final int code;

    private final String description;

    public GlobalException(StateCode stateCode) {
        super(stateCode.getMessage());
        this.code = stateCode.getCode();
        this.description = stateCode.getDescription();
    }

    public GlobalException(StateCode stateCode, String description) {
        super(stateCode.getMessage());
        this.code = stateCode.getCode();
        this.description = description;
    }

    public GlobalException(int code, String message,  String description) {
        super(message);
        this.code = code;
        this.description = description;
    }


}
