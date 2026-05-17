package com.myaccounts.service.myaccountsservice.services.imp;

import com.myaccounts.service.myaccountsservice.components.PropsSesionComponent;
import com.myaccounts.service.myaccountsservice.exceptions.ReLodingException;
import com.myaccounts.service.myaccountsservice.exceptions.RefreshException;
import com.myaccounts.service.myaccountsservice.models.dtos.LoginClaimsDto;
import com.myaccounts.service.myaccountsservice.models.dtos.UserDetailsDto;
import com.myaccounts.service.myaccountsservice.models.entities.LoginEntity;
import com.myaccounts.service.myaccountsservice.models.entities.UserEntity;
import com.myaccounts.service.myaccountsservice.repositories.LoginRepository;
import com.myaccounts.service.myaccountsservice.services.JwtService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.crypto.SecretKey;
import java.util.Base64;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

@Service
@Transactional
public class JwtServiceImp implements JwtService {

    @Value("${jwt.accesstoken.key}")
    private String accesskey;
    @Value("${jwt.logintoken.key}")
    private String logintokenkey;

    @Autowired
    private PropsSesionComponent component;
    @Autowired
    private LoginRepository loginRepository;

    private SecretKey getAccessKey() {
        return Keys.hmacShaKeyFor(Base64.getDecoder().decode(accesskey));
    }

    private SecretKey getLoginKey() {
        return Keys.hmacShaKeyFor(Base64.getDecoder().decode(logintokenkey));
    }

    @Override
    public String accessToken(UserDetailsDto userDetailsDto) {
        List<String> authorities = userDetailsDto.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority).collect(Collectors.toList());
        String username = userDetailsDto.getUsername();
        Claims claims = Jwts.claims().add("authorities", authorities).build();
        return Jwts.builder().signWith(getAccessKey())
                .subject(username)
                .claims(claims)
                .issuedAt(new Date())
                .expiration(component.getAccesstime())
                .compact();
    }

    @Override
    public UserDetailsDto validationAccessToken(String token) {
        try {
            Claims claims = Jwts.parser().verifyWith(getAccessKey()).build()
                    .parseSignedClaims(token).getPayload();
            @SuppressWarnings("unchecked")
            List<String> authoritiesname = (List<String>) claims.get("authorities");
            Collection<? extends GrantedAuthority> authorities = authoritiesname.stream()
                    .map(SimpleGrantedAuthority::new).collect(Collectors.toList());
            String username = claims.getSubject();
            return UserDetailsDto.builder()
                    .username(username).authorities(authorities).build();
        } catch (ExpiredJwtException e) {
            throw new RefreshException();
        }catch (Exception e){
            throw new ReLodingException();
        }
    }

    @Override
    public String loginToken(UserEntity user) {
        LoginEntity loginEntity = LoginEntity.builder().user(user).build();
        LoginEntity newLoginEntity = loginRepository.save(loginEntity);
        Claims claims = Jwts.claims().add("id", String.valueOf(newLoginEntity.getId())).build();
        String token = Jwts.builder().signWith(getLoginKey())
                .claims(claims)
                .issuedAt(new Date())
                .expiration(component.getLoginTime())
                .subject(user.getUsername()).compact();
        newLoginEntity.setJwt(token);
        loginRepository.save(newLoginEntity);
        return token;
    }

    @Override
    public LoginClaimsDto validationLoginToken(String token) {
        LoginEntity loginEntity = getLoginEntity(token);
        return LoginClaimsDto.builder()
                .username(loginEntity.getUser().getUsername())
                .idLogin(loginEntity.getId()).build();
    }

    @Override
    public void logout(String token) {
        LoginEntity loginEntity = getLoginEntity(token);
        loginEntity.setActive(false);
    }

    private LoginEntity getLoginEntity(String token) {
       try{
           var claims = Jwts.parser().verifyWith(getLoginKey()).build()
                   .parseSignedClaims(token).getPayload();
           Long idlogin = Long.parseLong((String) claims.get("id"));
           String username = claims.getSubject();
           Optional<LoginEntity> loginEntity = loginRepository.findByIdAndUsername(idlogin, username);
           if(loginEntity.isEmpty() || !loginEntity.get().getActive())
               throw new ReLodingException();
           return loginEntity.get();
       }catch (ExpiredJwtException e){
           throw new ReLodingException();
       }
    }
}