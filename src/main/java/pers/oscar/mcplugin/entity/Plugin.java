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
import java.util.Objects;
import java.util.Set;

@Setter
@Getter
@RequiredArgsConstructor
@Entity
@Table(name = "Plugins")
@NamedEntityGraph(name = "Plugin.Graph",
        attributeNodes = {@NamedAttributeNode("authors")})
@Where(clause = "deleted=0")
public class Plugin {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(length = 64, nullable = false)
    @NotNull(message = "名字不能为空")
    @NotBlank(message = "名字不能为空")
    @Size(max = 48, message = "名字过长")
    private String name;

    @Column(nullable = false)
    private ServerType serverType;

    @Column(length = 64, nullable = false)
    @NotNull(message = "版本不能为空")
    @NotBlank(message = "不能为空")
    @Size(max = 48, message = "过长")
    private String serverVersion;

    @Column(nullable = false)
    private PluginType pluginType;

    @Column(nullable = false)
    @NotNull(message = "不能为空")
    @NotBlank(message = "不能为空")
    @Size(max = 230, message = "过长")
    private String depends;

    @Column(nullable = false)
    @NotNull(message = "不能为空")
    @NotBlank(message = "不能为空")
    @Size(max = 230, message = "过长")
    private String download;

    @Lob
    @Basic(fetch = FetchType.LAZY)
    @Column(columnDefinition = "Text")
    @NotNull(message = "前言不能为空")
    @NotBlank(message = "前言不能为空")
    @Size(max = 4096, message = "过长")
    private String preface;

    @Lob
    @Basic(fetch = FetchType.LAZY)
    @Column(columnDefinition = "Text")
    @NotNull(message = "功能列表不能为空")
    @NotBlank(message = "功能列表不能为空")
    @Size(max = 4096, message = "过长")
    private String functionList;

    @Lob
    @Basic(fetch = FetchType.LAZY)
    @Column(columnDefinition = "MediumText", nullable = false)
    @NotNull(message = "使用方法不能为空")
    @NotBlank(message = "使用方法不能为空")
    @Size(max = 4096, message = "过长")
    private String usageMethod;

    @Lob
    @Basic(fetch = FetchType.LAZY)
    @Column(columnDefinition = "MediumText", nullable = false)
    @NotNull(message = "更新记录不能为空")
    @NotBlank(message = "更新记录不能为空")
    @Size(max = 4096, message = "过长")
    private String updateRecord;

    @Column(nullable = false)
    @JsonFormat(locale="zh", timezone="GMT+8", pattern="yyyy-MM-dd HH:mm:ss")
    private Date publishTime;

    @Column(nullable = false)
    @JsonFormat(locale="zh", timezone="GMT+8", pattern="yyyy-MM-dd HH:mm:ss")
    private Date updateTime;

    @Column(nullable = false)
    private boolean deleted;

    @ManyToMany()
    @JoinTable(name = "publishment")
    private Set<User> authors;

    @OneToOne(mappedBy = "plugin", fetch = FetchType.LAZY)
    private PluginDocument pluginDocument;

    @OneToMany(mappedBy = "plugin", cascade = CascadeType.ALL)
    private Set<Comment> comments;

    public enum PluginType {
        Fun, RPG, Comprehensive, Information, Manage,
        Economic, Security, API, Other
    }

    public enum ServerType {
        CraftBukkit, Paper, Spigot, CatServer, Sponge, BungeeCord,
        Other
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Plugin plugin = (Plugin) o;
        return id == plugin.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
