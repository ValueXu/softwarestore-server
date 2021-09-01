package cn.jxufe.valuexu.softwarestoreserver.controller;

import cn.jxufe.valuexu.softwarestoreserver.dao.ScoresRepository;
import cn.jxufe.valuexu.softwarestoreserver.dao.SoftwareRepository;
import cn.jxufe.valuexu.softwarestoreserver.dao.UserRepository;
import cn.jxufe.valuexu.softwarestoreserver.domain.Scores;
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
@RequestMapping("scores")
public class ScoresController {
    @Resource
    private ScoresRepository scoresRepository;
    @Resource
    private SoftwareRepository softwareRepository;

    @Resource
    private UserRepository userRepository;

    void recomputeScores(Software software) {
        //            重新统计软件平均分数
        long softwareId = software.getId();
        List<Scores> scoresAllBySoftwareId = scoresRepository.findAllBySoftwareId(softwareId);
        double sum = 0;
        for (int i = 0; i < scoresAllBySoftwareId.size(); i++) {
            Scores item = scoresAllBySoftwareId.get(i);
            sum += item.getScore();
        }
        software.setScore(sum / scoresAllBySoftwareId.size());
        softwareRepository.save(software);
    }

    @PostMapping("/add")
    @ResponseBody
    public ResponseBodyContent<HashMap> addScores(
            @RequestParam(name = "username") String username,
            @RequestParam(name = "software_id") long softwareId,
            @RequestParam(name = "score") int score,
            @RequestParam(name = "comment") String comment
    ) {
        ResponseBodyContent<HashMap> content = new ResponseBodyContent<>();
        try {
            Optional<Software> softwareFromSQL = softwareRepository.findById(softwareId);
            if (!softwareFromSQL.isPresent()) {
                throw new Exception("您评论的软件不存在");
            }
            Optional<User> userFromSQL = userRepository.findByUsername(username);
            if (!userFromSQL.isPresent()) {
                throw new Exception("用户名不存在");
            }
            Scores scores;
            List<Scores> scoresList = scoresRepository.findAllByUsernameAndSoftwareId(username, softwareId);
            if (scoresList.size() != 0) {
                scores = scoresList.get(0);
                scores.setScore(score);
                scores.setComment(comment);
            } else {
                scores = new Scores(username, softwareId, score, comment);
            }
            scoresRepository.save(scores);

            recomputeScores(softwareFromSQL.get());

        } catch (Exception e) {
            content.setCode(-1);
            content.setMsg(e.getMessage());
        }
        return content;
    }


    @RequestMapping("/delete")
    @ResponseBody
    public ResponseBodyContent<HashMap> deleteScore(
            @RequestParam(name = "software_id") long softwareId,
            @RequestParam(name = "username") String username
    ) {
        ResponseBodyContent<HashMap> content = new ResponseBodyContent<>();
        try {
            Optional<Software> softwareFromSQL = softwareRepository.findById(softwareId);
            List<Scores> scoresList = scoresRepository.findAllByUsernameAndSoftwareId(username, softwareId);
            for (int i = 0; i < scoresList.size(); i++) {
                Scores item = scoresList.get(i);
                scoresRepository.delete(item);
            }
            recomputeScores(softwareFromSQL.get());
        } catch (Exception e) {
            content.setCode(-1);
            content.setMsg(e.getMessage());
        }
        return content;
    }

    @RequestMapping("/getAllBySoftware")
    @ResponseBody
    public ResponseBodyContent<List> getAllByUsername(@RequestParam(name = "id") long softwareId) {
        ResponseBodyContent<List> content = new ResponseBodyContent<>();
        try {
            List<Scores> scoresList = scoresRepository.findAllBySoftwareId(softwareId);
            content.setResult(scoresList);
        } catch (Exception e) {
            content.setCode(-1);
            content.setMsg(e.getMessage());
        }
        return content;
    }

    @RequestMapping("/getAllByUsername")
    @ResponseBody
    public ResponseBodyContent<List> getAllByUsername(@RequestParam(name = "username") String username) {
        ResponseBodyContent<List> content = new ResponseBodyContent<>();
        try {
            List<Scores> scoresList = scoresRepository.findAllByUsername(username);
            List<HashMap> list = new ArrayList<>();

            for (int i = 0; i < scoresList.size(); i++) {
                Scores scores = scoresList.get(i);
                HashMap<String, Object> map = new HashMap<>();
                map.put("id", scores.getId());
                map.put("comment", scores.getComment());
                map.put("score", scores.getScore());
                map.put("software_id", scores.getSoftwareId());
                Optional<Software> softwareFromSQL = softwareRepository.findById(scores.getSoftwareId());
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

}
