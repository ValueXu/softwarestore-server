package cn.jxufe.valuexu.softwarestoreserver.controller;

import cn.jxufe.valuexu.softwarestoreserver.dao.UserRepository;
import cn.jxufe.valuexu.softwarestoreserver.domain.User;
import cn.jxufe.valuexu.softwarestoreserver.response.ResponseBodyContent;
import cn.jxufe.valuexu.softwarestoreserver.security.JwtUtil;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("user")
public class UserController {

    @Resource
    private UserRepository userRepository;

    @PostMapping("/signIn")
    @ResponseBody
    public ResponseBodyContent<HashMap> signIn(@RequestParam(name = "username") String username, @RequestParam(name = "password") String password) {
        Optional<User> user = userRepository.findByUsername(username);
        HashMap<String, Object> map = new HashMap<>();
        ResponseBodyContent<HashMap> content = new ResponseBodyContent<HashMap>();
        if (user.isPresent() && user.get().getPassword().equals(password)) {
            content.setCode(0);
            content.setMsg("success");
            HashMap<String, Object> userInfo = new HashMap<>();
            User info = user.get();
            userInfo.put("username", info.getUsername());
            userInfo.put("email", info.getEmail());
            userInfo.put("type", info.getType());
            userInfo.put("name", info.getName());
            userInfo.put("create_time", info.getCreateTime());
            userInfo.put("update_time", info.getUpdateTime());
            map.put("userInfo", userInfo);
            String token = JwtUtil.generateToken(username, info.getType());
            map.put("token", token);
            content.setResult(map);
        } else {
            content.setCode(-1);
            content.setMsg("用户名或密码错误");
        }
        return content;
    }

    @PostMapping("/signUp")
    @ResponseBody
    public ResponseBodyContent<HashMap> signUp(@RequestParam(name = "username") String username,
                                               @RequestParam(name = "password") String password,
                                               @RequestParam(name = "name") String name,
                                               @RequestParam(name = "email") String email,
                                               @RequestParam(name = "type") int type) {
        ResponseBodyContent<HashMap> content = new ResponseBodyContent<HashMap>();
        try {
            if ((int) type != 3 && (int) type != 4) {
                throw new Exception("用户类型错误");
            }
            Optional<User> userFromSQL = userRepository.findByUsername(username);
            User user;
            Date date = new Date();
            Timestamp timestamp = new Timestamp(date.getTime());
            if (userFromSQL.isPresent()) {
                throw new Exception("用户名已存在");
            }
            user = new User();

            user.setUsername(username);
            user.setPassword(password);
            user.setName(name);
            user.setEmail(email);
            user.setType(type);
            ;
            user.setCreateTime(timestamp);
            user.setUpdateTime(timestamp);
            userRepository.save(user);
        } catch (Exception e) {
            content.setCode(-1);
            content.setMsg(e.getMessage());
        }
        return content;
    }

    @PostMapping("/update")
    @ResponseBody
    public ResponseBodyContent<HashMap> update(@RequestParam(name = "username") String username,
                                               @RequestParam(name = "password") String password,
                                               @RequestParam(name = "name") String name,
                                               @RequestParam(name = "email") String email,
                                               @RequestParam(name = "type") int type) {
        ResponseBodyContent<HashMap> content = new ResponseBodyContent<HashMap>();
        try {
            if (type == 0 || type == 3) {
                throw new Exception("选择的用户类型错误");
            }
            Optional<User> userFromSQL = userRepository.findByUsername(username);
            User user;
            Date date = new Date();
            Timestamp timestamp = new Timestamp(date.getTime());
            if (!userFromSQL.isPresent()) {
                throw new Exception("用户不存在");
            }
            user = userFromSQL.get();
            user.setUsername(username);
            user.setPassword(password);
            user.setName(name);
            user.setEmail(email);
            user.setType(type);
            user.setUpdateTime(timestamp);
            userRepository.save(user);
        } catch (Exception e) {
            content.setCode(-1);
            content.setMsg(e.getMessage());
        }
        return content;
    }

    @RequestMapping("/getAll")
    @ResponseBody
    public ResponseBodyContent<List> getAll() {
        ResponseBodyContent<List> content = new ResponseBodyContent<>();
        try {
            List<User> userList = userRepository.findAll();
            content.setResult(userList);
        } catch (Exception e) {
            content.setCode(-1);
            content.setMsg(e.getMessage());
        }
        return content;
    }

    @PostMapping("/delete")
    @ResponseBody
    public ResponseBodyContent<HashMap> delete(@RequestParam(name = "username") String username
    ) {
        ResponseBodyContent<HashMap> content = new ResponseBodyContent<>();
        try {
            Optional<User> userFromSQL = userRepository.findByUsername(username);
            if (!userFromSQL.isPresent()) {
                throw new Exception("用户不存在");
            }
            userRepository.delete(userFromSQL.get());
        } catch (Exception e) {
            content.setCode(-1);
            content.setMsg(e.getMessage());
        }
        return content;
    }

}
