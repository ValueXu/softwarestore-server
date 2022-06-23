package cn.jxufe.valuexu.softwarestoreserver.scheduler;

import cn.jxufe.valuexu.softwarestoreserver.associationRuleMining.AssociationRuleMining;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class AssociationScheduler {

    private static Logger logger = LoggerFactory.getLogger(AssociationScheduler.class);
    private final static long taskFrequency = 1000L * 60L * 5L;

    public AssociationScheduler() {
        this.task();
    }

    @Scheduled(initialDelay = 1000L, fixedRate = taskFrequency)
    public void task() {
        try {
            logger.info("start to association rule mining task");
            AssociationRuleMining.start(0.05, 0.6);
        } catch (Exception e) {
            logger.error("run association rule mining error");
        }


    }
}
