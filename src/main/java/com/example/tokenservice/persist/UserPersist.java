package com.example.tokenservice.persist;

import java.util.Optional;

import org.springframework.stereotype.Service;

import com.example.tokenservice.constant.Errors;
import com.example.tokenservice.exception.CommonException;
import com.example.tokenservice.model.User;
import com.example.tokenservice.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserPersist {

	private final UserRepository userRepository;

	public void save(User user) {
		this.userRepository.save(user);
	}

	public User findById(String id) {
		Optional<User> user = this.userRepository.findById(id);
		if (user.isEmpty()) {
			throw CommonException.of(Errors.USER_NOT_FOUND_ERR);
		}
		return user.get();
	}

	public Optional<User> findByEmail(String email) {
		return this.userRepository.findByEmail(email);
	}
}
