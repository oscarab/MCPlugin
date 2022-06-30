package pers.oscar.mcplugin.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.transaction.annotation.Transactional;
import pers.oscar.mcplugin.entity.User;
import pers.oscar.mcplugin.repository.UserRepository;

import java.util.Optional;

public class UserDetailsConverter implements UserDetailsService {

    private UserRepository userRepository;

    @Autowired
    public void setUserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * 登录时根据用户名在数据库中查找
     * @param username 用户名称
     * @return 用户
     */
    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) {
        Optional<User> optional = userRepository.findByUsername(username);
        if (optional.isPresent()) {
            User user = optional.get();
            return user.buildUserDetails();
        }
        throw new UsernameNotFoundException("找不到用户");
    }
}
