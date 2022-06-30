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

@Setter
@Getter
@RequiredArgsConstructor
@Entity
@Table(name = "documents_copy")
@NamedEntityGraph(name = "PluginDocumentCopy.Graph",
        attributeNodes = {@NamedAttributeNode("pluginDocument")})
@Where(clause = "deleted=0")
public class PluginDocumentCopy {
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
    @JsonFormat(locale="zh", timezone="GMT+8", pattern="yyyy-MM-dd HH:mm:ss")
    private Date createdTime;

    @Column(nullable = false)
    private boolean deleted;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User author;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "doc_id")
    @NotNull(message = "所属插件文档不能为空")
    private PluginDocument pluginDocument;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PluginDocumentCopy copy = (PluginDocumentCopy) o;
        return id == copy.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
