package pers.oscar.mcplugin.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;
import pers.oscar.mcplugin.entity.PluginDocument;
import pers.oscar.mcplugin.entity.PluginDocumentCopy;
import pers.oscar.mcplugin.entity.User;

import java.util.Optional;

public interface DocumentCopyRepository extends JpaRepository<PluginDocumentCopy, Long> {

    Optional<PluginDocumentCopy> findByAuthorAndPluginDocument(User user, PluginDocument pluginDocument);
    boolean existsByAuthorAndPluginDocument(User user, PluginDocument pluginDocument);

    @EntityGraph(value = "PluginDocumentCopy.Graph", type = EntityGraph.EntityGraphType.FETCH)
    Page<PluginDocumentCopy> findByAuthor(User user, Pageable pageable);

    @Modifying
    @Transactional
    @Query(value = "update documents_copy set deleted=1 where doc_id=?1", nativeQuery = true)
    void deleteCopyByDocument(long id);
}
