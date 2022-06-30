package pers.oscar.mcplugin.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import pers.oscar.mcplugin.entity.User;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String name);
    List<User> findByUsernameLike(String name);
    Optional<User> findById(Long id);
    boolean existsUserByUsername(String name);
    boolean existsUserByEmail(String email);

    @Modifying
    @Transactional
    @Query(value = "update users set ip=?2 where id=?1", nativeQuery = true)
    void updateIP(long id, String ip);
}
