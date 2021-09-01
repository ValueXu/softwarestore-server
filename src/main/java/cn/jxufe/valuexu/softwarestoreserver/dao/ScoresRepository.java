package cn.jxufe.valuexu.softwarestoreserver.dao;

import cn.jxufe.valuexu.softwarestoreserver.domain.Scores;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ScoresRepository extends JpaRepository<Scores,Long> {
    List<Scores> findAllByUsernameAndSoftwareId(String username, long softwareId);
    List<Scores> findAllBySoftwareId(long softwareId);
    List<Scores> findAllByUsername(String username);
}
