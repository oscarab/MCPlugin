package pers.oscar.mcplugin.security.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import pers.oscar.mcplugin.entity.SimpleUser;
import pers.oscar.mcplugin.exception.AuthException;
import pers.oscar.mcplugin.result.ResponseCode;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.time.LocalDateTime;

public class LoginFilter extends AbstractAuthenticationProcessingFilter {
    public LoginFilter(AuthenticationManager authenticationManager) {
        super(new AntPathRequestMatcher("/auth/login"));
        setAuthenticationManager(authenticationManager);
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request,
                                                HttpServletResponse response) throws AuthenticationException, IOException {
        SimpleUser simpleUser = new ObjectMapper().readValue(request.getInputStream(), SimpleUser.class);
        HttpSession session = request.getSession();
        String captcha = (String) session.getAttribute("image_captcha");
        LocalDateTime expire = (LocalDateTime) session.getAttribute("image_expire");
        if (captcha == null
                || expire.isBefore(LocalDateTime.now())
                || !captcha.equals(simpleUser.getCaptcha())) {
            throw new AuthException(ResponseCode.LOGIN_FAIL, "验证码错误");
        }
        session.removeAttribute("image_captcha");
        session.removeAttribute("image_expire");

        UsernamePasswordAuthenticationToken authRequest;
        authRequest = new UsernamePasswordAuthenticationToken(simpleUser.getUsername(), simpleUser.getPassword());
        authRequest.setDetails(authenticationDetailsSource.buildDetails(request));

        return this.getAuthenticationManager().authenticate(authRequest);
    }
}
