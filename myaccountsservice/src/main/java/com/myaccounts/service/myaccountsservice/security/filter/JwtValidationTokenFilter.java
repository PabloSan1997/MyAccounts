package com.myaccounts.service.myaccountsservice.security.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import com.myaccounts.service.myaccountsservice.exceptions.RefreshException;
import com.myaccounts.service.myaccountsservice.models.dtos.ErrorDto;
import com.myaccounts.service.myaccountsservice.models.dtos.UserDetailsDto;
import com.myaccounts.service.myaccountsservice.services.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collection;

public class JwtValidationTokenFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final ObjectMapper objectMapper = Jackson2ObjectMapperBuilder.json().build();

    public JwtValidationTokenFilter(AuthenticationManager authenticationManager,
            JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
            HttpServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        String header = request.getHeader("Authorization");
        if (header == null || !header.startsWith("Bearer ")) {
            chain.doFilter(request, response);
            return;
        }
        String token = header.replace("Bearer ", "");

        try{
            UserDetailsDto userDetails = jwtService.validationAccessToken(token);
            String username = userDetails.getUsername();
            Collection<? extends GrantedAuthority> authorities = userDetails.getAuthorities();
            Authentication authenticationtoken = new UsernamePasswordAuthenticationToken(
                username, null, authorities);
            SecurityContextHolder.getContext().setAuthentication(authenticationtoken);
            chain.doFilter(request, response);
        }
        catch (RefreshException e){
            ErrorDto errorDto = new ErrorDto(HttpStatus.UNAUTHORIZED, e.getMessage());
            response.setStatus(errorDto.getStatusCode());
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            response.getWriter().write(objectMapper.writeValueAsString(errorDto));
        }
        catch (Exception e){
            chain.doFilter(request, response);
        }
    }
}