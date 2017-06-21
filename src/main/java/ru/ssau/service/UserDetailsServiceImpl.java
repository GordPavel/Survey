package ru.ssau.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import ru.ssau.domain.User;

import java.util.HashSet;
import java.util.Set;

@Service
public class UserDetailsServiceImpl implements UserDetailsService{

    @Autowired
    private UserService userService;

    @Override
    public UserDetails loadUserByUsername( String login ) throws UsernameNotFoundException{
        try{
            User user = userService.getUser( login ).orElseThrow( () -> new UsernameNotFoundException( login ) );
            Set<GrantedAuthority> roles = new HashSet<>();
            roles.add( new SimpleGrantedAuthority( user.getRole().name() ) );
            return new org.springframework.security.core.userdetails.User( user.getLogin(), user.getPassword(), roles );
        }catch( InterruptedException e ){
            e.printStackTrace();
            return new org.springframework.security.core.userdetails.User( "", "" , new HashSet<>() );
        }
    }
}
