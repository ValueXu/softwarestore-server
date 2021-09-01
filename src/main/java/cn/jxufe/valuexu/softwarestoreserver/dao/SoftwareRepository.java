package cn.jxufe.valuexu.softwarestoreserver.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import cn.jxufe.valuexu.softwarestoreserver.domain.Software;

import java.util.List;
import java.util.Optional;

public interface SoftwareRepository extends JpaRepository<Software, Long> {
    List<Software> findAllByType(int type);
    List<Software> findAllByAuthor(String author);
    List<Software> findAllByName(String name);
    Optional<Software> findByName(String name);
}
