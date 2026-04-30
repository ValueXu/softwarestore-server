package cn.jxufe.valuexu.softwarestoreserver.controller;

import cn.jxufe.valuexu.softwarestoreserver.dao.AssociationRuleRepository;
import cn.jxufe.valuexu.softwarestoreserver.dao.SoftwareRepository;
import cn.jxufe.valuexu.softwarestoreserver.dao.UserRepository;
import cn.jxufe.valuexu.softwarestoreserver.domain.Software;
import cn.jxufe.valuexu.softwarestoreserver.domain.User;
import cn.jxufe.valuexu.softwarestoreserver.dto.WrappedResDto;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

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
    public WrappedResDto<List> findAll() {
        WrappedResDto<List> content = new WrappedResDto<>();
        try {
            List<Software> list = softwareRepository.findAll();
            content.setResult(list);
        } catch (Exception e) {
            content.setCode(-1);
            content.setMsg(e.getMessage());
        }
        return content;
    }

    @RequestMapping("/getById")
    public WrappedResDto<Software> getById(long id) {
        WrappedResDto<Software> content = new WrappedResDto<>();
        try {
            Optional<Software> software = softwareRepository.findById(id);
            software.ifPresent(content::setResult);
        } catch (Exception e) {
            content.setCode(-1);
            content.setMsg(e.getMessage());
        }
        return content;
    }

    @RequestMapping("/getByName")
    public WrappedResDto<List> getByName(String name) {
        WrappedResDto<List> content = new WrappedResDto<>();
        try {
            List<Software> list = softwareRepository.findAllByName(name);
            content.setResult(list);
        } catch (Exception e) {
            content.setCode(-1);
            content.setMsg(e.getMessage());
        }
        return content;
    }

    @RequestMapping("/getAllByType")
    public WrappedResDto<List> getAllByType(int type) {
        WrappedResDto<List> content = new WrappedResDto<>();
        try {
            List<Software> list = softwareRepository.findAllByType(type);
            content.setResult(list);
        } catch (Exception e) {
            content.setCode(-1);
            content.setMsg(e.getMessage());
        }
        return content;
    }

    @RequestMapping("/getAllByAuthor")
    public WrappedResDto<List> getByAuthor(@RequestParam(name = "username") String author) {
        WrappedResDto<List> content = new WrappedResDto<>();
        try {
            List<Software> list = softwareRepository.findAllByAuthor(author);
            content.setResult(list);
        } catch (Exception e) {
            content.setCode(-1);
            content.setMsg(e.getMessage());
        }
        return content;
    }

    @RequestMapping("/getAssociationRulesById")
    public WrappedResDto<List> getAssociationRulesById(long id) {
        WrappedResDto<List> content = new WrappedResDto<>();
        try {
            List<Long> softwareIds = associationRuleRepository
                    .findAllBySoftwareId0OrSoftwareId1OrSoftwareId2OrSoftwareId3OrSoftwareId4(id, id, id, id, id);
            List<Optional<Software>> softwareList = new ArrayList<>();
            for (Long softwareId : softwareIds) {
                Optional<Software> current = softwareRepository.findById(softwareId);
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
    public WrappedResDto<HashMap> uploadSoftware(@RequestParam(name = "name") String name,
                                                 @RequestParam(name = "author") String author,
                                                 @RequestParam(name = "description") String description,
                                                 @RequestParam(name = "type") int type,
                                                 @RequestParam(name = "download_url") String downloadUrl,
                                                 @RequestParam(name = "imgUrl", defaultValue = "/assets/software_imgs/0.png") String imgUrl) {
        WrappedResDto<HashMap> content = new WrappedResDto<>();
        int code = 1;
        String msg = "success";
        try {
            List<Software> softwareFromSQL = softwareRepository.findAllByName(name);
            if (!softwareFromSQL.isEmpty()) {
                throw new Exception("已存在相同名称的软件，请更换软件名");
            }
            Optional<User> user = userRepository.findByUsername(author);
            if (user.isEmpty()) {
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
    public WrappedResDto<HashMap> updateSoftware(@RequestParam(name = "id") long id,
                                                 @RequestParam(name = "name") String name,
                                                 @RequestParam(name = "author") String author,
                                                 @RequestParam(name = "description") String description,
                                                 @RequestParam(name = "type") int type,
                                                 @RequestParam(name = "download_url") String downloadUrl,
                                                 @RequestParam(name = "img_url", defaultValue = "/assets/software_imgs/0.png") String imgUrl) {
        WrappedResDto<HashMap> content = new WrappedResDto<>();
        try {
            Optional<Software> softwareFromSQL = softwareRepository.findById(id);

            if (softwareFromSQL.isEmpty()) {
                throw new Exception("该软件未注册，请先上传");
            }
            Optional<User> user = userRepository.findByUsername(author);
            if (user.isEmpty()) {
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

    @PostMapping("/deleteSoftware")
    WrappedResDto<HashMap> deleteSoftware(@RequestParam(name = "software_id") long softwareId) {
        WrappedResDto<HashMap> content = new WrappedResDto<>();
        try {
            softwareRepository.deleteById(softwareId);
        } catch (Exception e) {
            content.setCode(-1);
            content.setMsg(e.getMessage());
        }

        return content;
    }
}
