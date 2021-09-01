package cn.jxufe.valuexu.softwarestoreserver.dao;

import cn.jxufe.valuexu.softwarestoreserver.domain.Record;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RecordRepository extends JpaRepository<Record,Long> {
    List<Record> findAllByUserName(String username);
}
