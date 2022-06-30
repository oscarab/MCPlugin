package pers.oscar.mcplugin.security.handle;

import com.alibaba.fastjson.JSON;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import pers.oscar.mcplugin.common.utils.IpUtil;
import pers.oscar.mcplugin.entity.SimpleUser;
import pers.oscar.mcplugin.repository.UserRepository;
import pers.oscar.mcplugin.result.ResponseResult;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class LoginSuccessHandle implements AuthenticationSuccessHandler {

    private UserRepository userRepository;

    @Autowired
    public void setUserRepository(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException {
        SimpleUser principle =  (SimpleUser) authentication.getPrincipal();
        userRepository.updateIP(principle.getId(), IpUtil.getIpAddress(authentication));

        response.setContentType("text/json;charset=utf-8");
        ResponseResult result = ResponseResult.success(principle);
        response.getWriter().write(JSON.toJSONString(result));
    }
}
