package com.example.service;

import com.example.entity.User;
import com.example.Repository.UserRepository;
import com.example.util.TokenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private WebSocketService webSocketService;

    public boolean register(String username, String password, String identityCode) {
        if (userRepository.existsById(username)) {
            return false; // 用户名已存在
        }
        User newUser = new User(username, password, "user", identityCode);
        userRepository.save(newUser);
        System.out.println("[注册成功] 用户：" + username);
        webSocketService.broadcast("{\"type\":\"refresh\"}");
        return true;
    }

    public String login(String username, String password) {
        Optional<User> optionalUser = userRepository.findById(username);
        if (optionalUser.isPresent() && optionalUser.get().getPassword().equals(password)) {
            String token = TokenUtil.generateToken(username);
            System.out.println("[登录成功] 用户：" + username);
            return token;
        }
        return null;
    }

    public User getUserByUsername(String username) {
        return userRepository.findById(username).orElse(null);
    }

    public User getUserByIdentityCode(String identityCode) {
        return userRepository.findByIdentityCode(identityCode);
    }

    public String getIdentityCodeByUsername(String username) {
        User user = userRepository.findById(username).orElse(null);
        return user != null ? user.getIdentityCode() : null;
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public boolean deleteUser(String username) {
        if (userRepository.existsById(username)) {
            if ("admin".equals(username)) {
                System.out.println("[删除用户失败] 管理员用户无法删除！");
                return false;
            }
            userRepository.deleteById(username);
            System.out.println("[删除用户成功] 用户：" + username);
            webSocketService.broadcast("{\"type\":\"refresh\"}");
            return true;
        }
        System.out.println("[删除用户失败] 用户：" + username + " 不存在！");
        return false;
    }
}
