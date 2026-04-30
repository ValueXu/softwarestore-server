package cn.jxufe.valuexu.softwarestoreserver.controller;

import cn.jxufe.valuexu.softwarestoreserver.config.ResCodeEnum;
import cn.jxufe.valuexu.softwarestoreserver.dao.RecordRepository;
import cn.jxufe.valuexu.softwarestoreserver.dao.SoftwareRepository;
import cn.jxufe.valuexu.softwarestoreserver.dao.UserRepository;
import cn.jxufe.valuexu.softwarestoreserver.domain.Record;
import cn.jxufe.valuexu.softwarestoreserver.domain.Software;
import cn.jxufe.valuexu.softwarestoreserver.domain.User;
import cn.jxufe.valuexu.softwarestoreserver.dto.WrappedResDto;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
import java.util.*;

@RestController
@RequestMapping("record")
public class RecordController {
    private static final Logger logger = LoggerFactory.getLogger(RecordController.class);

    private final RecordRepository recordRepository;
    private final SoftwareRepository softwareRepository;
    private final UserRepository userRepository;

    public RecordController(RecordRepository recordRepository, SoftwareRepository softwareRepository, UserRepository userRepository) {
        this.recordRepository = recordRepository;
        this.softwareRepository = softwareRepository;
        this.userRepository = userRepository;
    }

    @GetMapping("/getAll")
    public WrappedResDto<List<Record>> getAll() {
        try {
            List<Record> records = recordRepository.findAll();
            return WrappedResDto.success(records);
        } catch (Exception e) {
            logger.error("get all record error", e);
            return WrappedResDto.failed(ResCodeEnum.UNKNOWN_ERR);
        }
    }

    @GetMapping("/getAllByUsername")
    public WrappedResDto<List> getAllByUsername(
            @RequestParam(name = "username") String username
    ) {
        try {
            List<Record> records = recordRepository.findAllByUserName(username);
            List<HashMap> list = new ArrayList<>();
            for (Record record : records) {
                HashMap<String, Object> map = new HashMap<>();
                map.put("id", record.getId());
                map.put("username", record.getUserName());
                map.put("software_id", record.getSoftwareId());
                map.put("time", record.getTime());
                Optional<Software> softwareFromSQL = softwareRepository.findById(record.getSoftwareId());
                softwareFromSQL.ifPresent(software -> map.put("name", software.getName()));
                list.add(map);
            }
            return WrappedResDto.success(list);
        } catch (Exception e) {
            return WrappedResDto.failed(ResCodeEnum.UNKNOWN_ERR);
        }
    }

    @PostMapping("/add")
    public WrappedResDto<String> addRecord(@RequestParam(name = "software_id") long softwareId,
                                            @RequestParam(name = "username") String username) {
        try {
            Optional<Software> softwareFromSQL = softwareRepository.findById(softwareId);
            if (softwareFromSQL.isEmpty()) {
                throw new Exception("软件不存在");
            }
            Optional<User> userFromSQL = userRepository.findByUsername(username);
            if (userFromSQL.isEmpty()) {
                throw new Exception("用户名不存在");
            }
            Date date = new Date();
            Record record = new Record();
            record.setSoftwareId(softwareId);
            record.setUserName(username);
            record.setTime(new Timestamp(date.getTime()));
            recordRepository.save(record);
            return WrappedResDto.success();
        } catch (Exception e) {
            return WrappedResDto.failed(ResCodeEnum.UNKNOWN_ERR);
        }
    }

    @DeleteMapping("/delete")
    public WrappedResDto<String> deleteRecord(@RequestParam(name = "record_id") long recordId) {
        try {
            recordRepository.deleteById(recordId);
            return WrappedResDto.success();
        } catch (Exception e) {
            logger.error("delete record error", e);
            return WrappedResDto.failed(ResCodeEnum.UNKNOWN_ERR);
        }
    }
}
