package pers.oscar.mcplugin.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import pers.oscar.mcplugin.common.captcha.ImageCode;
import pers.oscar.mcplugin.entity.SimpleUser;
import pers.oscar.mcplugin.service.UserAuthService;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private UserAuthService userAuthService;

    @Autowired
    public void setUserService(UserAuthService userAuthService) {
        this.userAuthService = userAuthService;
    }

    @PostMapping("/register")
    public SimpleUser register(HttpServletRequest request, @RequestBody SimpleUser user) {
        return userAuthService.userRegister(request, user);
    }

    @GetMapping(value = "/captcha")
    public void sendImageCode(HttpServletRequest request, HttpServletResponse response) throws IOException {
        ImageCode captcha = userAuthService.createImageCode(request.getSession());

        request.getSession().setAttribute("captchaImage", captcha.getText());
        response.setContentType("image/png");
        ImageIO.write(captcha.getImage(), "jpeg", response.getOutputStream());
    }

    @PostMapping("/email")
    public boolean sendValidateCode(HttpServletRequest request, @RequestBody SimpleUser user) {
        userAuthService.sendEmail(request.getSession(), user.getEmail());
        return true;
    }
}
