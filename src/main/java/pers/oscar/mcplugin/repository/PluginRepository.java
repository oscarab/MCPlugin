package pers.oscar.mcplugin.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import pers.oscar.mcplugin.entity.Plugin;

import java.util.List;

@Repository
public interface PluginRepository extends JpaRepository<Plugin, Long> {

    @EntityGraph(value = "Plugin.Graph", type = EntityGraph.EntityGraphType.FETCH)
    Page<Plugin> findAll(Pageable pageable);

    @Query(value = "select * from plugins " +
            "left join publishment p on plugins.id = p.own_plugins_id " +
            "where p.authors_id=?1 " +
            "and deleted=0 " +
            "order by plugins.publish_time DESC",
            nativeQuery = true)
    Page<Plugin> findByAuthor(long id, Pageable pageable);

    @Query(value = "select * from plugins " +
            "where name like ?1 and " +
            "plugin_type like ?2 and " +
            "server_type like ?3 and " +
            "server_version like ?4 and " +
            "deleted=0 order by update_time DESC ", nativeQuery = true)
    Page<Plugin> findBySome(String name, String pluginType, String serverType, String serverVersion, Pageable pageable);

    @Query(value = "select * from plugins " +
            "where (name like ?1 or " +
            "preface like ?1 or " +
            "usage_method like ?1 or " +
            "function_list like ?1) and " +
            "deleted=0 " +
            "order by update_time DESC ", nativeQuery = true)
    Page<Plugin> findByAll(String info, Pageable pageable);

    @Modifying
    @Transactional
    @Query(value = "update plugins set deleted=1 where id=?1", nativeQuery = true)
    void deletePlugin(long id);

    @Query(value = "select plugins.id, " +
            "plugins.name, " +
            "plugins.plugin_type, " +
            "plugins.server_type, " +
            "plugins.server_version, " +
            "plugins.update_time, " +
            "plugins.publish_time, " +
            "plugins.deleted, " +
            "plugins.depends, " +
            "plugins.download, " +
            "COUNT(c.id) as comments " +
            "from plugins " +
            "left join comments c on plugins.id = c.plugin_id " +
            "where plugins.deleted=0 and c.deleted=0 " +
            "group by plugins.id " +
            "order by comments DESC", nativeQuery = true)
    Page<Plugin> findHot(Pageable pageable);
}
