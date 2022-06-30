package pers.oscar.mcplugin.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import pers.oscar.mcplugin.entity.Notify;

public interface NotifyRepository extends JpaRepository<Notify, Long> {

    @Query(value = "select *, COUNT(DISTINCT notifies.id) " +
            "from notifies left join documents d on d.id = notifies.doc_id " +
            "left join cooperation c on d.id = c.co_documents_id " +
            "where c.cooperators_id=?1 or notifies.user_id=?1 " +
            "group by notifies.id " +
            "order by notifies.time DESC", nativeQuery = true)
    Page<Notify> getMyNotifies(long id, Pageable pageable);

    @Query(value = "select count(id) " +
            "from notifies " +
            "where state=0 and copy_id=?1", nativeQuery = true)
    long existsWait(long id);
}
