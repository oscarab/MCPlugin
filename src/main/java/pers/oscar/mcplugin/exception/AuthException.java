package pers.oscar.mcplugin.exception;

import lombok.Getter;
import org.springframework.security.core.AuthenticationException;
import pers.oscar.mcplugin.result.ResponseCode;

@Getter
public class AuthException extends AuthenticationException {

    private final ResponseCode code;

    public AuthException() {
        this(ResponseCode.LOGIN_FAIL);
    }

    public AuthException(ResponseCode code) {
        this(code, code.getMessage());
    }

    public AuthException(ResponseCode code, String message) {
        super(message);
        this.code = code;
    }
}
