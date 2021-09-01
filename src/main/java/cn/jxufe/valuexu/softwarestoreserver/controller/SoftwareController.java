package cn.jxufe.valuexu.softwarestoreserver.controller;


import cn.jxufe.valuexu.softwarestoreserver.dao.AssociationRuleRepository;
import cn.jxufe.valuexu.softwarestoreserver.dao.SoftwareRepository;
import cn.jxufe.valuexu.softwarestoreserver.dao.UserRepository;
import cn.jxufe.valuexu.softwarestoreserver.domain.Software;
import cn.jxufe.valuexu.softwarestoreserver.domain.User;
import cn.jxufe.valuexu.softwarestoreserver.response.ResponseBodyContent;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("software")
public class SoftwareController {
    @Resource
    private SoftwareRepository softwareRepository;
    @Resource
    private UserRepository userRepository;
    @Resource
    private AssociationRuleRepository associationRuleRepository;

    @RequestMapping("/getAllSoftware")
    @ResponseBody
    public ResponseBodyContent<List> findAll() {
        ResponseBodyContent<List> content = new ResponseBodyContent<List>();
        try {
            List<Software> list = new ArrayList<Software>();
            list = softwareRepository.findAll();
            content.setResult(list);
        } catch (Exception e) {
            content.setCode(-1);
            content.setMsg(e.getMessage());
        }
        return content;
    }

    @RequestMapping("/getById")
    @ResponseBody
    public ResponseBodyContent<Software> getById(long id) {
        ResponseBodyContent<Software> content = new ResponseBodyContent<Software>();
        try {
            Optional<Software> software = softwareRepository.findById(id);
            content.setResult(software.get());
        } catch (Exception e) {
            content.setCode(-1);
            content.setMsg(e.getMessage());
        }
        return content;
    }

    @RequestMapping("/getByName")
    @ResponseBody
    public ResponseBodyContent<List> getByName(String name) {
        ResponseBodyContent<List> content = new ResponseBodyContent<List>();
        try {
            List<Software> list = new ArrayList<Software>();
            list = softwareRepository.findAllByName(name);
            content.setResult(list);
        } catch (Exception e) {
            content.setCode(-1);
            content.setMsg(e.getMessage());
        }
        return content;
    }

    @RequestMapping("/getAllByType")
    @ResponseBody
    public ResponseBodyContent<List> getAllByType(int type) {
        ResponseBodyContent<List> content = new ResponseBodyContent<List>();
        try {
            List<Software> list = new ArrayList<Software>();
            list = softwareRepository.findAllByType(type);
            content.setResult(list);
        } catch (Exception e) {
            content.setCode(-1);
            content.setMsg(e.getMessage());
        }
        return content;
    }

    @RequestMapping("/getAllByAuthor")
    @ResponseBody
    public ResponseBodyContent<List> getByAuthor(@RequestParam(name = "username") String author) {
        ResponseBodyContent<List> content = new ResponseBodyContent<List>();
        try {
            List<Software> list = new ArrayList<Software>();
            list = softwareRepository.findAllByAuthor(author);
            content.setResult(list);
        } catch (Exception e) {
            content.setCode(-1);
            content.setMsg(e.getMessage());
        }
        return content;
    }

    @RequestMapping("/getAssociationRulesById")
    @ResponseBody
    public ResponseBodyContent<List> getAssociationRulesById(long id) {
        ResponseBodyContent<List> content = new ResponseBodyContent<List>();
        try {
            List<Software> list = new ArrayList<Software>();
            List<Long> softwareIds = associationRuleRepository.findAllBySoftwareId0(id);
            List<Optional> softwareList = new ArrayList<>();
            int length = softwareIds.size();
            for (int i = 0; i < length; i++) {
                Optional<Software> current = softwareRepository.findById((long) softwareIds.indexOf(i));
                softwareList.add(current);
            }
            content.setResult(softwareList);
        } catch (Exception e) {
            content.setCode(-1);
            content.setMsg(e.getMessage());
        }
        return content;

    }

    @PostMapping("/uploadSoftware")
    @ResponseBody
    public ResponseBodyContent<HashMap> uploadSoftware(@RequestParam(name = "name") String name,
                                                       @RequestParam(name = "author") String author,
                                                       @RequestParam(name = "description") String description,
                                                       @RequestParam(name = "type") int type,
                                                       @RequestParam(name = "download_url") String downloadUrl,
                                                       @RequestParam(name = "imgUrl", defaultValue = "/assets/software_imgs/0.png") String imgUrl
    ) {
        ResponseBodyContent<HashMap> content = new ResponseBodyContent<HashMap>();
        int code = 1;
        String msg = "success";
        try {
            List<Software> softwareFromSQL = softwareRepository.findAllByName(name);
            if (softwareFromSQL.size() != 0) {
                throw new Exception("已存在相同名称的软件，请更换软件名");
            }
            Optional<User> user = userRepository.findByUsername(author);
            if (!user.isPresent()) {
                throw new Exception("当前开发者未注册，请先注册开发者");
            }
            Software software = new Software(name, author, description, type, imgUrl, downloadUrl);
            softwareRepository.save(software);
        } catch (Exception e) {
            code = -1;
            msg = e.getMessage();
        }
        content.setCode(code);
        content.setMsg(msg);
        return content;
    }

    @PostMapping("/updateSoftware")
    @ResponseBody
    public ResponseBodyContent<HashMap> updateSoftware(@RequestParam(name = "id") long id,
                                                       @RequestParam(name = "name") String name,
                                                       @RequestParam(name = "author") String author,
                                                       @RequestParam(name = "description") String description,
                                                       @RequestParam(name = "type") int type,
                                                       @RequestParam(name = "download_url") String downloadUrl,
                                                       @RequestParam(name = "img_url", defaultValue = "/assets/software_imgs/0.png") String imgUrl
    ) {
        ResponseBodyContent<HashMap> content = new ResponseBodyContent<HashMap>();
        try {
            Optional<Software> softwareFromSQL = softwareRepository.findById(id);

            if (!softwareFromSQL.isPresent()) {
                throw new Exception("该软件未注册，请先上传");
            }
            Optional<User> user = userRepository.findByUsername(author);
            if (!user.isPresent()) {
                throw new Exception("当前开发者未注册，请先注册开发者");
            }
            Software software = softwareFromSQL.get();
            software.setName(name);
            software.setAuthor(author);
            software.setDescription(description);
            software.setType(type);
            software.setDownloadUrl(downloadUrl);
            software.setImgUrl(imgUrl);
            softwareRepository.save(software);

        } catch (Exception e) {
            content.setCode(-1);
            content.setMsg(e.getMessage());
        }
        return content;
    }


    @RequestMapping("/deleteSoftware")
    @ResponseBody
    ResponseBodyContent<HashMap> deleteSoftware(@RequestParam(name = "software_id") long softwareId) {
        ResponseBodyContent<HashMap> content = new ResponseBodyContent<>();
        try {
            softwareRepository.deleteById(softwareId);
        } catch (Exception e) {
            content.setCode(-1);
            content.setMsg(e.getMessage());
        }

        return content;
    }

}
