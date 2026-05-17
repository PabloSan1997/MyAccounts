package com.myaccounts.service.myaccountsservice.services.imp;

import com.myaccounts.service.myaccountsservice.exceptions.MyBadRequestException;
import com.myaccounts.service.myaccountsservice.exceptions.ReLodingException;
import com.myaccounts.service.myaccountsservice.models.dtos.DoubleJwtDto;
import com.myaccounts.service.myaccountsservice.models.dtos.JwtDto;
import com.myaccounts.service.myaccountsservice.models.dtos.LoginClaimsDto;
import com.myaccounts.service.myaccountsservice.models.dtos.LoginDto;
import com.myaccounts.service.myaccountsservice.models.dtos.RegisterDto;
import com.myaccounts.service.myaccountsservice.models.dtos.UserDetailsDto;
import com.myaccounts.service.myaccountsservice.models.dtos.UserInfoDto;
import com.myaccounts.service.myaccountsservice.models.entities.RoleEntity;
import com.myaccounts.service.myaccountsservice.models.entities.UserEntity;
import com.myaccounts.service.myaccountsservice.repositories.RoleRepository;
import com.myaccounts.service.myaccountsservice.repositories.UserRepository;
import com.myaccounts.service.myaccountsservice.services.JwtService;
import com.myaccounts.service.myaccountsservice.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class UserServiceImp implements UserService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private JwtService jwtService;
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private RoleRepository roleRepository;

    @Override
    public DoubleJwtDto register(RegisterDto registerDto) {
        if (userRepository.findByUsername(registerDto.getUsername()).isPresent()) {
            throw new MyBadRequestException("Username already exists");
        }

        UserEntity user = new UserEntity();
        user.setUsername(registerDto.getUsername());
        user.setPassword(passwordEncoder.encode(registerDto.getPassword()));
        user.setNickname(registerDto.getNickname());

        RoleEntity userRole = roleRepository.findByName("USER")
                .orElseThrow(() -> new MyBadRequestException("Role not found"));
        user.setRoles(List.of(userRole));

        user = userRepository.save(user);

        UserDetailsDto userDetailsDto = UserDetailsDto.builder()
                .username(user.getUsername())
                .password(user.getPassword())
                .build();
        userDetailsDto.setAuthoritiesAsRoles(user.getRoles());

        String jwt = jwtService.accessToken(userDetailsDto);
        String loginToken = jwtService.loginToken(user);

        return DoubleJwtDto.builder().accessToken(jwt).loginToken(loginToken).build();
    }

    @Override
    public DoubleJwtDto login(LoginDto loginDto) {
        Authentication authtoken = new UsernamePasswordAuthenticationToken(
            loginDto.getUsername(), loginDto.getPassword());
        try{
            UserDetailsDto userDetailsDto = (UserDetailsDto) authenticationManager
                .authenticate(authtoken).getPrincipal();
            assert userDetailsDto != null;
            UserEntity user = userDetailsDto.getUser();
            String jwt = jwtService.accessToken(userDetailsDto);
            String loginToken = jwtService.loginToken(user);
            return DoubleJwtDto.builder().accessToken(jwt).loginToken(loginToken).build();
        }catch (Exception ex){
            throw new MyBadRequestException("Incorrect username or password");
        }
    }

    @Override
    public UserInfoDto getUserInfo() {
        String username = (String) SecurityContextHolder.getContext()
            .getAuthentication().getPrincipal();
        UserEntity user = userRepository.findByUsername(username)
            .orElseThrow(ReLodingException::new);
        return new UserInfoDto(username, user.getNickname());
    }

    @Override
    public void logout(String token) {
        jwtService.logout(token);
    }

    @Override
    public JwtDto refreshToken(String token) {
        LoginClaimsDto claimsDto = jwtService.validationLoginToken(token);
        UserEntity user = userRepository.findByUsername(claimsDto.getUsername())
            .orElseThrow(ReLodingException::new);
        UserDetailsDto userDetailsDto = UserDetailsDto.builder()
            .username(claimsDto.getUsername()).build();
        userDetailsDto.setAuthoritiesAsRoles(user.getRoles());
        String jwt = jwtService.accessToken(userDetailsDto);
        return JwtDto.builder().token(jwt).build();
    }
}