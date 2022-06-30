package pers.oscar.mcplugin.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import pers.oscar.mcplugin.entity.Role;
import pers.oscar.mcplugin.entity.SimpleUser;
import pers.oscar.mcplugin.entity.User;
import pers.oscar.mcplugin.exception.ResourceNotFoundException;
import pers.oscar.mcplugin.repository.RoleRepository;
import pers.oscar.mcplugin.repository.UserRepository;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {
    private UserRepository userRepository;
    private RoleRepository roleRepository;

    @Autowired
    public void setRoleRepository(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @Autowired
    public void setUserRepository(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * 使用用户名在数据库中寻找用户
     * @param name 用户名
     * @return 用户对象
     */
    public User findUserByName(String name) {
        Optional<User> user = userRepository.findByUsername(name);
        if (user.isPresent()) {
            return user.get();
        }
        throw new ResourceNotFoundException("未找到用户");
    }

    /**
     * 用id寻找用户
     * @param id id
     * @return 用户
     */
    public User findUserByID(long id) {
        Optional<User> user = userRepository.findById(id);
        if (user.isPresent()) {
            return user.get();
        }
        throw new ResourceNotFoundException("未找到用户");
    }

    public List<User> findUserLikeName(String name) {
        List<User> users = userRepository.findByUsernameLike("%" + name + "%");
        if (users.size() > 10) {
            return users.subList(0, 10);
        }
        return users;
    }

    public User getCurrentUser() {
        SimpleUser simpleUser = (SimpleUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return findUserByID(simpleUser.getId());
    }

    public User getCurrentDetail() {
        Object object = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (!(object instanceof SimpleUser)) {
            User user = new User();
            user.setId(0);
            return user;
        }
        SimpleUser simpleUser = (SimpleUser) object;
        User user = new User();
        user.setId(simpleUser.getId());
        Role role = new Role();
        role.setName(simpleUser.getAuthorities().get(0).getAuthority());
        user.setRole(role);
        return user;
    }

    /**
     * 修改用户信息
     * @param info 用户
     */
    public void editUser(User info) {
        User user = getCurrentUser();
        user.setAvatar(info.getAvatar());
        userRepository.save(user);
    }

    /**
     * 修改用户的角色
     * @param id 用户id
     * @param roleName 角色名称
     */
    public void editUserRole(long id, String roleName) {
        Role role = roleRepository.findByName(roleName).orElse(null);
        User user = findUserByID(id);

        user.setRole(role);
        userRepository.save(user);
    }

}
