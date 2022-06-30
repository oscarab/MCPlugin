package pers.oscar.mcplugin.exception;

import pers.oscar.mcplugin.result.ResponseCode;

public class AccessDenyException extends RuntimeException{
    private final ResponseCode code;

    public AccessDenyException() {
        this(ResponseCode.ACCESS_DENIED.getMessage());
    }

    public AccessDenyException(String message) {
        super(message);
        code = ResponseCode.ACCESS_DENIED;
    }
}
