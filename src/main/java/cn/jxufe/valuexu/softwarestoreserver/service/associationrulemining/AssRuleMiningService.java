package cn.jxufe.valuexu.softwarestoreserver.service.associationrulemining;

import cn.jxufe.valuexu.softwarestoreserver.dao.AssociationRuleRepository;
import cn.jxufe.valuexu.softwarestoreserver.dao.RecordRepository;
import cn.jxufe.valuexu.softwarestoreserver.dao.UserRepository;
import cn.jxufe.valuexu.softwarestoreserver.domain.Record;
import cn.jxufe.valuexu.softwarestoreserver.domain.User;
import io.swagger.v3.oas.annotations.servers.Server;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class AssRuleMiningService {
    private static final Logger logger = LoggerFactory.getLogger(AssRuleMiningService.class);

    private final AssociationRuleRepository associationRuleRepository;

    private final RecordRepository recordRepository;

    private final UserRepository userRepository;

    public AssRuleMiningService(AssociationRuleRepository associationRuleRepository, RecordRepository recordRepository, UserRepository userRepository) {
        this.associationRuleRepository = associationRuleRepository;
        this.recordRepository = recordRepository;
        this.userRepository = userRepository;
    }

    /**
     * 从数据库读取下载记录并转为关联规则挖掘使用格式
     *
     * @return
     */
    public List<List<String>> getRecordsForRulesMining() {
        List<List<String>> recordFromDB = new ArrayList<>();
        try {
            List<User> users = userRepository.findAll();
            for (User user : users) {
                String username = user.getUsername();
                List<Record> usersDownRecords = recordRepository.findAllByUserName(username);
                List<String> userDownSoftwareIds = new ArrayList<>();
                for (Record usersDownRecord : usersDownRecords) {
                    userDownSoftwareIds.add(Long.toString(usersDownRecord.getSoftwareId()));
                }
                // 只加入有下载记录的用户的记录进去
                if (!userDownSoftwareIds.isEmpty()) {
                    recordFromDB.add(userDownSoftwareIds);
                }
            }
        } catch (Exception e) {
            logger.error("读取数据库数据出错", e);
        }
        return recordFromDB;
    }
}
