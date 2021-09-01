package cn.jxufe.valuexu.softwarestoreserver.controller;

import cn.jxufe.valuexu.softwarestoreserver.dao.RecordRepository;
import cn.jxufe.valuexu.softwarestoreserver.dao.SoftwareRepository;
import cn.jxufe.valuexu.softwarestoreserver.dao.UserRepository;
import cn.jxufe.valuexu.softwarestoreserver.domain.Record;
import cn.jxufe.valuexu.softwarestoreserver.domain.Software;
import cn.jxufe.valuexu.softwarestoreserver.domain.User;
import cn.jxufe.valuexu.softwarestoreserver.response.ResponseBodyContent;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.sql.Timestamp;
import java.util.*;

@RestController
@RequestMapping("record")
public class RecordController {
    @Resource
    private RecordRepository recordRepository;
    @Resource
    private SoftwareRepository softwareRepository;
    @Resource
    private UserRepository userRepository;

    @RequestMapping("/getAll")
    @ResponseBody
    public ResponseBodyContent<List> getAll() {
        ResponseBodyContent<List> content = new ResponseBodyContent<>();
        try {
            List<Record> records = recordRepository.findAll();
            content.setResult(records);
        } catch (Exception e) {
            content.setCode(-1);
            content.setMsg(e.getMessage());
        }
        return content;
    }

    @RequestMapping("/getAllByUsername")
    @ResponseBody
    public ResponseBodyContent<List> getAllByUsername(
            @RequestParam(name = "username") String username
    ) {
        ResponseBodyContent<List> content = new ResponseBodyContent<>();
        try {
            List<Record> records = recordRepository.findAllByUserName(username);
            List<HashMap> list = new ArrayList<>();
            for (int i = 0; i < records.size(); i++) {
                Record record = records.get(i);
                HashMap<String, Object> map = new HashMap<>();
                map.put("id", record.getId());
                map.put("username", record.getUserName());
                map.put("software_id", record.getSoftwareId());
                map.put("time", record.getTime());
                Optional<Software> softwareFromSQL = softwareRepository.findById(record.getSoftwareId());
                if (softwareFromSQL.isPresent()) {
                    map.put("name", softwareFromSQL.get().getName());
                }
                list.add(map);
            }
            content.setResult(list);
        } catch (Exception e) {
            content.setCode(-1);
            content.setMsg(e.getMessage());
        }
        return content;
    }

    @PostMapping("/add")
    @ResponseBody
    public ResponseBodyContent<HashMap> addRecord(@RequestParam(name = "software_id") long softwareId,
                                                  @RequestParam(name = "username") String username) {
        ResponseBodyContent<HashMap> content = new ResponseBodyContent<>();
        try {
            Optional<Software> softwareFromSQL = softwareRepository.findById(softwareId);
            if (!softwareFromSQL.isPresent()) {
                throw new Exception("软件不存在");
            }
            Optional<User> userFromSQL = userRepository.findByUsername(username);
            if (!userFromSQL.isPresent()) {
                throw new Exception("用户名不存在");
            }
            Date date = new Date();
            Record record = new Record();
            record.setSoftwareId(softwareId);
            record.setUserName(username);
            record.setTime(new Timestamp(date.getTime()));
            recordRepository.save(record);
        } catch (Exception e) {
            content.setCode(-1);
            content.setMsg(e.getMessage());
        }

        return content;
    }

    @RequestMapping("/delete")
    @ResponseBody
    public ResponseBodyContent<HashMap> deleteRecord(@RequestParam(name = "record_id") long recordId) {
        ResponseBodyContent<HashMap> content = new ResponseBodyContent<>();
        try {
            recordRepository.deleteById(recordId);
        } catch (Exception e) {
            content.setCode(-1);
            content.setMsg(e.getMessage());
        }
        return content;
    }
}
