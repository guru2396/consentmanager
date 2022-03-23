package com.consent.consentmanager.config;

import com.consent.consentmanager.service.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class JwtFilter extends OncePerRequestFilter {

    @Autowired
    private JwtService jwtService;

    @Value("${client.id}")
    private String clientId;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String auth=request.getHeader("Authorization");
        if(auth!=null && !"".equals(auth) && auth.startsWith("Bearer ")){
            String subject=jwtService.extractID(auth);
            if(clientId.equals(subject) && SecurityContextHolder.getContext().getAuthentication()==null){
                UsernamePasswordAuthenticationToken ut=new UsernamePasswordAuthenticationToken(clientId,null,null);
                ut.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(ut);
            }
        }
        filterChain.doFilter(request,response);
    }
}
