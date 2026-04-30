package cn.jxufe.valuexu.softwarestoreserver.scheduler;

import cn.jxufe.valuexu.softwarestoreserver.dao.AssociationRuleRepository;
import cn.jxufe.valuexu.softwarestoreserver.service.associationrulemining.AssRuleMiningService;
import cn.jxufe.valuexu.softwarestoreserver.service.associationrulemining.AssociationRuleMinner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class AssociationScheduler {

    private static final Logger logger = LoggerFactory.getLogger(AssociationScheduler.class);
    private static final long TASK_FREQUENCY = 1000L * 60L * 5L;
    private static final double MIN_SUPPORT = 0.05;
    private static final double MIN_CONFIDENCE = 0.6;

    private final AssRuleMiningService assRuleMiningService;
    private final AssociationRuleRepository associationRuleRepository;

    public AssociationScheduler(AssRuleMiningService assRuleMiningService, AssociationRuleRepository associationRuleRepository) {
        this.assRuleMiningService = assRuleMiningService;
        this.associationRuleRepository = associationRuleRepository;
    }

    @Scheduled(initialDelay = 1000L, fixedRate = TASK_FREQUENCY)
    public void task() {
        try {
            logger.info("start to association rule mining task");
            List<List<String>> records = assRuleMiningService.getRecordsForRulesMining();
            AssociationRuleMinner associationRuleMinner = AssociationRuleMinner.builder(records, associationRuleRepository)
                    .minConfidence(MIN_CONFIDENCE)
                    .minSupport(MIN_SUPPORT)
                    .build();
            associationRuleMinner.start();
            logger.info("association rule mining task end successfully");
        } catch (Exception e) {
            logger.error("run association rule mining error");
        }
    }
}
