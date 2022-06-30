package pers.oscar.mcplugin.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Date;

@Setter
@Getter
@RequiredArgsConstructor
@Entity
@Table(name = "comments")
@NamedEntityGraph(name = "Comment.Graph",
        attributeNodes = {@NamedAttributeNode("user")})
@Where(clause = "deleted=0")
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Lob
    @Basic(fetch = FetchType.EAGER)
    @Column(columnDefinition = "text", nullable = false)
    @NotNull(message = "不能为空")
    @NotBlank(message = "不能为空")
    @Size(max = 1024, message = "内容过长")
    private String content;

    @Column(nullable = false)
    @JsonFormat(locale="zh", timezone="GMT+8", pattern="yyyy-MM-dd HH:mm:ss")
    private Date publishTime;

    @Column(nullable = false)
    private boolean deleted;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "plugin_id")
    @NotNull(message = "所属插件为空")
    private Plugin plugin;

}
