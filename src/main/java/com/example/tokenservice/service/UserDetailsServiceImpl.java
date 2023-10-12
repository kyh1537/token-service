package com.example.tokenservice.service;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.example.tokenservice.dto.UserDetailsImpl;
import com.example.tokenservice.model.User;
import com.example.tokenservice.persist.UserPersist;
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