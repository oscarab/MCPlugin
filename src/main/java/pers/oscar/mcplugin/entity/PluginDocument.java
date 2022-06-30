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
@Table(name = "documents")
@Where(clause = "deleted=0")
public class PluginDocument {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Lob
    @Basic(fetch = FetchType.LAZY)
    @Column(columnDefinition = "MediumText", nullable = false)
    @NotNull(message = "说明不能为空")
    @NotBlank(message = "说明不能为空")
    @Size(max = 4096, message = "说明过长")
    private String explanation;

    @Lob
    @Basic(fetch = FetchType.LAZY)
    @Column(columnDefinition = "MediumText", nullable = false)
    @NotNull(message = "API介绍不能为空")
    @NotBlank(message = "API介绍不能为空")
    @Size(max = 4096, message = "API介绍过长")
    private String api;

    @Lob
    @Basic(fetch = FetchType.LAZY)
    @Column(columnDefinition = "MediumText", nullable = false)
    @NotNull(message = "问答不能为空")
    @NotBlank(message = "问答不能为空")
    @Size(max = 4096, message = "问答过长")
    private String faq;

    @Column(nullable = false)
    @JsonFormat(locale="zh", timezone="GMT+8", pattern="yyyy-MM-dd HH:mm:ss")
    private Date updateTime;

    @Column(nullable = false)
    private boolean deleted;

    @ManyToMany()
    @JoinTable(name = "cooperation")
    private Set<User> cooperators;

    @OneToMany(mappedBy = "pluginDocument")
    private Set<PluginDocumentCopy> copies;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "plugin_id")
    @NotNull(message = "所属插件不能为空")
    private Plugin plugin;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PluginDocument that = (PluginDocument) o;
        return id == that.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
