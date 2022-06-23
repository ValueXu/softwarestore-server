package cn.jxufe.valuexu.softwarestoreserver.dao;

import cn.jxufe.valuexu.softwarestoreserver.domain.AssociationRule;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AssociationRuleRepository extends JpaRepository<AssociationRule, Long> {
    List<Long> findAllBySoftwareId0OrSoftwareId1OrSoftwareId2OrSoftwareId3OrSoftwareId4(long softwareId0, long softwareId1, long softwareId2, long softwareId3, long softwareId4);
}

