package com.myaccounts.service.myaccountsservice.models.dtos;

import com.myaccounts.service.myaccountsservice.models.entities.RoleEntity;
import com.myaccounts.service.myaccountsservice.models.entities.UserEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.lang.Nullable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDetailsDto implements UserDetails {

    private String username;
    private String password;
    private Collection<? extends GrantedAuthority> authorities;
    @Getter
    private UserEntity user;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public @Nullable String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    public void setAuthoritiesAsRoles(List<RoleEntity> roles) {
        authorities = roles.stream()
                .map(p -> new SimpleGrantedAuthority("ROLE_"+p.getName())).toList();
    }
}