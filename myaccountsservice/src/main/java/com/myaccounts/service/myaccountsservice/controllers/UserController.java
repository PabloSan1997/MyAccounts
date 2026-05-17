package com.myaccounts.service.myaccountsservice.controllers;

import com.myaccounts.service.myaccountsservice.components.PropsSesionComponent;
import com.myaccounts.service.myaccountsservice.models.dtos.DoubleJwtDto;
import com.myaccounts.service.myaccountsservice.models.dtos.JwtDto;
import com.myaccounts.service.myaccountsservice.models.dtos.LoginDto;
import com.myaccounts.service.myaccountsservice.models.dtos.RegisterDto;
import com.myaccounts.service.myaccountsservice.models.dtos.UserInfoDto;
import com.myaccounts.service.myaccountsservice.services.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    private UserService userService;
    @Autowired
    private PropsSesionComponent component;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody @Valid LoginDto loginDto) {
        DoubleJwtDto res = userService.login(loginDto);
        JwtDto jwtDto = new JwtDto(res.getAccessToken());
        ResponseCookie cookie = ResponseCookie.from("the_cookie", res.getLoginToken())
                .sameSite(component.getSameStite())
                .httpOnly(component.getHttpOnly())
                .secure(component.getSecurity())
                .maxAge(component.getLoginTimeCookie())
                .path(component.getPath()).build();
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.SET_COOKIE, cookie.toString());
        return ResponseEntity.ok().headers(headers).body(jwtDto);
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody @Valid RegisterDto registerDto) {
        DoubleJwtDto res = userService.register(registerDto);
        JwtDto jwtDto = new JwtDto(res.getAccessToken());
        ResponseCookie cookie = ResponseCookie.from("the_cookie", res.getLoginToken())
                .sameSite(component.getSameStite())
                .httpOnly(component.getHttpOnly())
                .secure(component.getSecurity())
                .maxAge(component.getLoginTimeCookie())
                .path(component.getPath()).build();
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.SET_COOKIE, cookie.toString());
        return ResponseEntity.ok().headers(headers).body(jwtDto);
    }

    @GetMapping("/userinfo")
    public ResponseEntity<?> getUserInfo() {
        return ResponseEntity.ok(userService.getUserInfo());
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(@CookieValue(name = "the_cookie", required = false)
            String cookie) {
        if (cookie == null || cookie.isEmpty()) {
            throw new com.myaccounts.service.myaccountsservice.exceptions.ReLodingException();
        }
        return ResponseEntity.ok(userService.refreshToken(cookie));
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(@CookieValue(name = "the_cookie", required = false)
            String cookie) {
        if (cookie == null || cookie.isEmpty()) {
            throw new com.myaccounts.service.myaccountsservice.exceptions.ReLodingException();
        }
        userService.logout(cookie);
        ResponseCookie responseCookie = ResponseCookie.from("the_cookie", "")
                .sameSite(component.getSameStite())
                .httpOnly(component.getHttpOnly())
                .secure(component.getSecurity())
                .maxAge(0)
                .path(component.getPath()).build();
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.SET_COOKIE, responseCookie.toString());
        return ResponseEntity.noContent().headers(headers).build();
    }
}