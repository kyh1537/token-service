package com.example.videolab.service;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.example.videolab.dto.UserDetailsImpl;
import com.example.videolab.model.User;
import com.example.videolab.persist.UserPersist;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserPersist userPersist;

    @Override
    public UserDetails loadUserByUsername(String uid) throws UsernameNotFoundException {
        User user = this.userPersist.findById(uid, false);
        UserDetailsImpl userDetails = new UserDetailsImpl();
        userDetails.setUser(user);
        return userDetails;
    }
}