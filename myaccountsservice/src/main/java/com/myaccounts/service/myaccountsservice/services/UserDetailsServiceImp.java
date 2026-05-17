package com.myaccounts.service.myaccountsservice.services;

import com.myaccounts.service.myaccountsservice.models.dtos.UserDetailsDto;
import com.myaccounts.service.myaccountsservice.models.entities.UserEntity;
import com.myaccounts.service.myaccountsservice.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserDetailsServiceImp implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserEntity user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException(username));
        UserDetailsDto userDetailsDto = UserDetailsDto.builder()
                .user(user).username(username).password(user.getPassword()).build();
        userDetailsDto.setAuthoritiesAsRoles(user.getRoles());
        return userDetailsDto;
    }
}