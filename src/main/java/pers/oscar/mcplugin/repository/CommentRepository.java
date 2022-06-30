package pers.oscar.mcplugin.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;
import pers.oscar.mcplugin.entity.Comment;
import pers.oscar.mcplugin.entity.Plugin;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    @EntityGraph(value = "Comment.Graph", type = EntityGraph.EntityGraphType.FETCH)
    Page<Comment> findByPlugin(Plugin plugin, Pageable pageable);

    @Modifying
    @Transactional
    @Query(value = "update comments set deleted=1 where id=?1", nativeQuery = true)
    void deleteComment(long id);
}
