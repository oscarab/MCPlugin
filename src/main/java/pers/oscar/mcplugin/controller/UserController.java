package pers.oscar.mcplugin.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import pers.oscar.mcplugin.entity.User;
import pers.oscar.mcplugin.service.UserService;

import java.util.List;

@RestController
@RequestMapping("/user")
public class UserController {
    private UserService userService;

    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/{id}")
    public User getUserByID(@PathVariable long id) {
        User user = userService.findUserByID(id);

        // 不能看别人的敏感信息，例如ip
        User me = userService.getCurrentDetail();
        if (me.getId() != id) {
            user.setIp(null);
            user.setPassword(null);
            user.setEmail(null);
        }
        return user;
    }

    @GetMapping("/search/{name}")
    public List<User> searchUser(@PathVariable String name) {
        List<User> users = userService.findUserLikeName(name);
        for (User user : users) {
            user.setIp(null);
            user.setPassword(null);
            user.setEmail(null);
        }
        return users;
    }

    @PostMapping("/edit")
    public void editUser(@RequestBody User user) {
        userService.editUser(user);
    }
}
