package pers.oscar.mcplugin.exception;

import lombok.Getter;
import pers.oscar.mcplugin.result.ResponseCode;

@Getter
public class ResourceNotFoundException extends RuntimeException {

    private final ResponseCode code;

    public ResourceNotFoundException() {
        this(ResponseCode.NOT_FOUND);
    }

    public ResourceNotFoundException(ResponseCode code) {
        super(code.getMessage());
        this.code = code;
    }

    public ResourceNotFoundException(String message) {
        super(message);
        code = ResponseCode.NOT_FOUND;
    }
}
