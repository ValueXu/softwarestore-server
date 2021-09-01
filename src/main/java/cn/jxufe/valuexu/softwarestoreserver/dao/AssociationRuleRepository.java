package cn.jxufe.valuexu.softwarestoreserver.dao;

import cn.jxufe.valuexu.softwarestoreserver.domain.AssociationRule;
import cn.jxufe.valuexu.softwarestoreserver.domain.Software;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AssociationRuleRepository extends JpaRepository<AssociationRule,Long> {
    List<Long> findAllBySoftwareId0(long softwareId0);
}
