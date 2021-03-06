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
     * ?????????????????????
     * @param session session
     * @return ???????????????
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
     * ????????????????????????
     * @param email ????????????
     * @param session session
     */
    public void sendEmail (HttpSession session, String email) {
        String text = captchaProducer.createText();
        text += captchaProducer.createText();

        if (userRepository.existsUserByEmail(email)) {
            throw new AuthException(ResponseCode.REGISTER_FAIL, "???????????????");
        }

        // ???????????????????????????????????????????????????????????????
        LocalDateTime repeat = (LocalDateTime) session.getAttribute(email + "#repeat");
        if (repeat != null && !repeat.isBefore(LocalDateTime.now())) {
            throw new AuthException(ResponseCode.REGISTER_FAIL, "??????????????????");
        }

        // ??????????????????????????????session
        session.setAttribute(email + "#text", text);
        session.setAttribute(email + "#expire", LocalDateTime.now().plusMinutes(5));
        session.setAttribute(email + "#repeat", LocalDateTime.now().plusMinutes(1));

        // ?????????????????????
        SimpleMailMessage mail = new SimpleMailMessage();
        mail.setTo(email);
        mail.setSubject("?????????");
        mail.setText(text);
        mail.setFrom(mailFrom);
        mailSender.send(mail);
    }

    /**
     * ??????????????????
     * @param user ??????
     * @return ??????
     */
    public SimpleUser userRegister(HttpServletRequest request, SimpleUser user) {

        if (user == null || user.getUsername() == null) {
            throw new AuthException(ResponseCode.REGISTER_FAIL, "?????????????????????");
        }
        if (user.getPassword() == null) {
            throw new AuthException(ResponseCode.REGISTER_FAIL, "??????????????????");
        }
        if (user.getEmail() == null) {
            throw new AuthException(ResponseCode.REGISTER_FAIL, "??????????????????");
        }
        if (user.getCaptcha() == null) {
            throw new AuthException(ResponseCode.REGISTER_FAIL, "?????????????????????");
        }

        // ?????????????????????????????????
        if (userRepository.existsUserByUsername(user.getUsername())) {
            throw new AuthException(ResponseCode.REGISTER_FAIL);
        }

        // ???????????????
        HttpSession session = request.getSession();
        String captcha = (String) session.getAttribute(user.getEmail() + "#text");
        LocalDateTime expire = (LocalDateTime) session.getAttribute(user.getEmail() + "#expire");
        if (captcha == null || expire == null) {
            throw new AuthException(ResponseCode.REGISTER_FAIL, "???????????????");
        }
        if (!captcha.equals(user.getCaptcha())) {
            throw new AuthException(ResponseCode.REGISTER_FAIL, "???????????????");
        }
        if (expire.isBefore(LocalDateTime.now())) {
            throw new AuthException(ResponseCode.REGISTER_FAIL, "???????????????");
        }

        // ??????????????????????????????
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
