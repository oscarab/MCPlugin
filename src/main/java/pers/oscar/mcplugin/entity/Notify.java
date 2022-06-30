package pers.oscar.mcplugin.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;

@Getter
@Setter
@RequiredArgsConstructor
@Entity
@Table(name = "Notifies")
public class Notify {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false)
    private State state;

    @Column(nullable = false)
    @JsonFormat(locale="zh", timezone="GMT+8", pattern="yyyy-MM-dd HH:mm:ss")
    private Date time;

    @OneToOne()
    @JoinColumn(name = "user_id")
    private User user;

    @OneToOne()
    @JoinColumn(name = "doc_id")
    private PluginDocument document;

    @OneToOne()
    @JoinColumn(name = "copy_id")
    private PluginDocumentCopy copy;

    @OneToOne()
    @JoinColumn(name = "plugin_id")
    private Plugin plugin;

    public enum State {
        WAITE, REJECT, ACCEPT
    };
}
