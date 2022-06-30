package pers.oscar.mcplugin.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import java.util.*;

@Getter
@Setter
@RequiredArgsConstructor
@Entity
@Table(name = "Users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(length = 32, nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private String avatar;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    @JsonFormat(locale="zh", timezone="GMT+8", pattern="yyyy-MM-dd HH:mm:ss")
    private Date registerTime;

    @Column(nullable = false)
    @JsonFormat(locale="zh", timezone="GMT+8", pattern="yyyy-MM-dd HH:mm:ss")
    private Date loginTime;

    @Column(length = 32, nullable = false)
    private String email;

    @Column(length = 32, nullable = false)
    private String ip;

    @Column(nullable = false)
    private int credit;

    @ManyToMany(mappedBy = "authors")
    private Set<Plugin> ownPlugins;

    @ManyToMany(mappedBy = "cooperators")
    private Set<PluginDocument> coDocuments;

    @OneToMany(mappedBy = "author", cascade = CascadeType.ALL)
    private Set<PluginDocumentCopy> coDocumentsCopy;

    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private Role role;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private Set<Comment> comments;

    public UserDetails buildUserDetails() {
        List<SimpleGrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority(role.getName()));

        SimpleUser simpleUser = new SimpleUser();
        simpleUser.setId(id);
        simpleUser.setUsername(username);
        simpleUser.setPassword(password);
        simpleUser.setEmail(email);
        simpleUser.setAuthorities(authorities);
        return simpleUser;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return id == user.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
