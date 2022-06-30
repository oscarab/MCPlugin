package pers.oscar.mcplugin.exception;

import pers.oscar.mcplugin.result.ResponseCode;

public class PostException extends RuntimeException {
    private final ResponseCode code;

    public PostException() {
        this(ResponseCode.POST_FAIL);
    }

    public PostException(ResponseCode code) {
        super(code.getMessage());
        this.code = code;
    }

    public PostException(String message) {
        super(message);
        code = ResponseCode.POST_FAIL;
    }
}
