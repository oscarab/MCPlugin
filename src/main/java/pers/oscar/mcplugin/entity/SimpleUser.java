package pers.oscar.mcplugin.entity;

import lombok.Data;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.validation.constraints.Email;
import java.util.List;

@Data
public class SimpleUser implements UserDetails {
    private long id;
    private String username;
    private String password;
    @Email
    private String email;
    private String captcha;

    private boolean accountNonExpired = true;
    private boolean accountNonLocked = true;
    private boolean credentialsNonExpired = true;
    private boolean enabled = true;
    private List<SimpleGrantedAuthority> authorities;
}
