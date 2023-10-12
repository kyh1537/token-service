package com.example.videolab.persist;

import java.util.Optional;
import org.springframework.stereotype.Service;

import com.example.videolab.constant.Errors;
import com.example.videolab.exception.CommonException;
import com.example.videolab.model.User;
import com.example.videolab.repository.UserRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserPersist {

    private final UserRepository userRepository;

    public void save(User user) {
        this.userRepository.save(user);
    }

    public User findById(String id, Boolean nullable) {
        Optional<User> user = this.userRepository.findById(id);
        if (!nullable && user.isEmpty()) {
            throw new CommonException(Errors.USER_NOT_FOUND_ERR);
        }
        return user.orElse(null);
    }

    public Optional<User> findByEmail(String email) {
        return this.userRepository.findByEmail(email);
    }
}
