package pers.oscar.mcplugin.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;
import pers.oscar.mcplugin.entity.PluginDocument;

public interface DocumentRepository extends JpaRepository<PluginDocument, Long> {

    @Query(value = "select * from documents " +
            "left join cooperation c on documents.id = c.co_documents_id " +
            "left join plugins p on documents.plugin_id = p.id " +
            "where c.cooperators_id=?1 " +
            "and documents.deleted=0 " +
            "order by documents.update_time DESC", nativeQuery = true)
    Page<PluginDocument> findByCooperator(long id, Pageable pageable);

    @Modifying
    @Transactional
    @Query(value = "update documents set deleted=1 where id=?1", nativeQuery = true)
    void deleteDocument(long id);

    @Modifying
    @Transactional
    @Query(value = "update documents set deleted=1 where plugin_id=?1", nativeQuery = true)
    void deleteDocumentByPlugin(long id);
}
