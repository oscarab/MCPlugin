package pers.oscar.mcplugin.service;

import com.google.code.kaptcha.impl.DefaultKaptcha;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import pers.oscar.mcplugin.common.captcha.ImageCode;
import pers.oscar.mcplugin.common.utils.IpUtil;
import pers.oscar.mcplugin.entity.Role;
import pers.oscar.mcplugin.entity.SimpleUser;
import pers.oscar.mcplugin.entity.User;
import pers.oscar.mcplugin.exception.AuthException;
import pers.oscar.mcplugin.repository.RoleRepository;
import pers.oscar.mcplugin.repository.UserRepository;
import pers.oscar.mcplugin.result.ResponseCode;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.awt.image.BufferedImage;
import java.time.LocalDateTime;
import java.util.Date;

@Service
@RequiredArgsConstructor
public class UserAuthService {
    @Value("${spring.mail.username}")
    private String mailFrom;
    private UserRepository userRepository;
    private RoleRepository roleRepository;
    private PasswordEncoder passwordEncoder;
    private DefaultKaptcha captchaProducer;
    private JavaMailSender mailSender;

    @Autowired
    public void setRoleRepository(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }
    @Autowired
    public void setUserRepository(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
    @Autowired
    public void setPasswordEncoder(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }
    @Autowired
    public void setCaptchaProducer(DefaultKaptcha defaultKaptcha) {
        this.captchaProducer = defaultKaptcha;
    }
    @Autowired
    public void setMailSender(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    /**
     * 生成图片验证码
     * @param session session
     * @return 图片验证码
     */
    public ImageCode createImageCode(HttpSession session) {
        ImageCode captcha = new ImageCode();
        String text = captchaProducer.createText();
        BufferedImage image = captchaProducer.createImage(text);

        captcha.setImage(image);
        captcha.setText(text);
        captcha.setExpireTime(LocalDateTime.now().plusMinutes(5));

        session.setAttribute("image_captcha", text);
        session.setAttribute("image_expire", captcha.getExpireTime());
        return captcha;
    }

    /**
     * 向邮箱发送验证码
     * @param email 邮箱地址
     * @param session session
     */
    public void sendEmail (HttpSession session, String email) {
        String text = captchaProducer.createText();
        text += captchaProducer.createText();

        if (userRepository.existsUserByEmail(email)) {
            throw new AuthException(ResponseCode.REGISTER_FAIL, "邮箱已存在");
        }

        // 若有以前发送过的记录，检查一下是否过于频繁
        LocalDateTime repeat = (LocalDateTime) session.getAttribute(email + "#repeat");
        if (repeat != null && !repeat.isBefore(LocalDateTime.now())) {
            throw new AuthException(ResponseCode.REGISTER_FAIL, "发送过于频繁");
        }

        // 将发送邮件的信息存入session
        session.setAttribute(email + "#text", text);
        session.setAttribute(email + "#expire", LocalDateTime.now().plusMinutes(5));
        session.setAttribute(email + "#repeat", LocalDateTime.now().plusMinutes(1));

        // 发送验证码邮件
        SimpleMailMessage mail = new SimpleMailMessage();
        mail.setTo(email);
        mail.setSubject("验证码");
        mail.setText(text);
        mail.setFrom(mailFrom);
        mailSender.send(mail);
    }

    /**
     * 用户注册服务
     * @param user 用户
     * @return 用户
     */
    public SimpleUser userRegister(HttpServletRequest request, SimpleUser user) {

        if (user == null || user.getUsername() == null) {
            throw new AuthException(ResponseCode.REGISTER_FAIL, "用户名不能为空");
        }
        if (user.getPassword() == null) {
            throw new AuthException(ResponseCode.REGISTER_FAIL, "密码不能为空");
        }
        if (user.getEmail() == null) {
            throw new AuthException(ResponseCode.REGISTER_FAIL, "邮箱不能为空");
        }
        if (user.getCaptcha() == null) {
            throw new AuthException(ResponseCode.REGISTER_FAIL, "验证码不能为空");
        }

        // 寻找是否已经存在该用户
        if (userRepository.existsUserByUsername(user.getUsername())) {
            throw new AuthException(ResponseCode.REGISTER_FAIL);
        }

        // 校验验证码
        HttpSession session = request.getSession();
        String captcha = (String) session.getAttribute(user.getEmail() + "#text");
        LocalDateTime expire = (LocalDateTime) session.getAttribute(user.getEmail() + "#expire");
        if (captcha == null || expire == null) {
            throw new AuthException(ResponseCode.REGISTER_FAIL, "验证码错误");
        }
        if (!captcha.equals(user.getCaptcha())) {
            throw new AuthException(ResponseCode.REGISTER_FAIL, "验证码错误");
        }
        if (expire.isBefore(LocalDateTime.now())) {
            throw new AuthException(ResponseCode.REGISTER_FAIL, "验证码失效");
        }

        // 通过校验，保存新用户
        User completeUser = new User();
        Role role = roleRepository.findByName("ROLE_server").orElse(null);
        String ip = IpUtil.getIpAddress(SecurityContextHolder.getContext().getAuthentication());
        completeUser.setUsername(user.getUsername());
        completeUser.setPassword(passwordEncoder.encode(user.getPassword()));
        completeUser.setEmail(user.getEmail());
        completeUser.setRole(role);
        completeUser.setRegisterTime(new Date());
        completeUser.setLoginTime(new Date());
        completeUser.setCredit(0);
        completeUser.setIp(ip);
        completeUser.setAvatar("null.jpg");
        userRepository.save(completeUser);

        return user;
    }
}
