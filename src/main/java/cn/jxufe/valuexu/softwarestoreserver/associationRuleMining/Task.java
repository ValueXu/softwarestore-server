package cn.jxufe.valuexu.softwarestoreserver.associationRuleMining;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class Task {

    public Task(){
        this.task();
    }

    @Scheduled(cron = "0 0/5 * * * ?")
    public void task() {
        System.out.println("执行定时任务");
        AssociationRuleMining.start(0.05, 0.6);
    }
}
