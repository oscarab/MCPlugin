package pers.oscar.mcplugin.security.handle;

import com.alibaba.fastjson.JSON;
import org.springframework.security.authentication.*;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;
import pers.oscar.mcplugin.exception.AuthException;
import pers.oscar.mcplugin.result.ResponseCode;
import pers.oscar.mcplugin.result.ResponseResult;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class LoginFailureHandle implements AuthenticationFailureHandler {
    @Override
    public void onAuthenticationFailure(HttpServletRequest request,
                                        HttpServletResponse response,
                                        AuthenticationException exception) throws IOException {
        response.setContentType("text/json;charset=utf-8");
        ResponseResult result = ResponseResult.fail(ResponseCode.LOGIN_FAIL);
        if (exception instanceof LockedException) {
            result.setMessage("账户被锁定，请联系管理员!");
        } else if (exception instanceof CredentialsExpiredException) {
            result.setMessage("密码过期，请联系管理员!");
        } else if (exception instanceof AccountExpiredException) {
            result.setMessage("账户过期，请联系管理员!");
        } else if (exception instanceof DisabledException) {
            result.setMessage("账户被禁用，请联系管理员!");
        } else if (exception instanceof AuthException) {
            result.setMessage(exception.getMessage());
        } else if (exception instanceof BadCredentialsException) {
            result.setMessage("用户名或密码错误");
        }
        response.setStatus(result.getStatus());
        response.getWriter().write(JSON.toJSONString(result));
    }
}
