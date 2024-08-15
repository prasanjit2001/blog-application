package com.blog.blog_app.security;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private JwtTokenHelper jwtTokenHelper;
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        //Get Token


        String requestToken=request.getHeader("Authorization");
        log.info("This is requestToken:::{}",requestToken);
        String username=null;
        String token=null;

        if(requestToken!=null && requestToken.startsWith("Bearer")){   //Token always starts with  Bearer

        token=  requestToken.substring(7); //Bearer 23455ufudc (Format)
            log.info("This is my token value:::{}",token);

            try {
                username = this.jwtTokenHelper.getUserNameFromToken(token);
            }
            catch(IllegalArgumentException e){
               log.info("Unable to get JWT Token");
            }
            catch(ExpiredJwtException e){
                log.info("Jwt Token Expired");
            }
            catch(MalformedJwtException e){  // This exception is thrown when the JWT string that is being validated is not correctly formatted
                log.info("Invalid JWT");
            }
        }
        else{
            log.info("Jwt token does not begin with Bearer");
        }

        //validate token
        if(username!=null && SecurityContextHolder.getContext().getAuthentication()==null){
            UserDetails userDetails=this.userDetailsService.loadUserByUsername(username);
            if(this.jwtTokenHelper.validateToken(token,userDetails)){

                //A new UsernamePasswordAuthenticationToken is created using the user details and their authorities. This token is used by Spring Security to represent the authenticated user.
                UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken=new UsernamePasswordAuthenticationToken(userDetails,null,userDetails.getAuthorities());

                //Additional details (such as request information) are set on the authentication token. This can include information like the remote address and session ID.
                usernamePasswordAuthenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
            }
            else{
                log.info("Invalid jwt token");
            }
        }
        else{
          log.info("Username is null or context is not null"  );
        }

        filterChain.doFilter(request,response);

    }
}
