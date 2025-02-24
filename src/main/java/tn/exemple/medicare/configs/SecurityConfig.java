package tn.exemple.medicare.configs;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import tn.exemple.medicare.services.impl.UserDetailsServices;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    public SecurityConfig(UserDetailsServices userDetailsServices) {
        this.userDetailsServices = userDetailsServices;
    }

    private final UserDetailsServices userDetailsServices;
    @Bean
    //dans ce code bech naamil filter yaani ay requette avant de passer au controller il faut passer avec ce filtre
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception{
 return http
         .csrf(AbstractHttpConfigurer::disable)
         .authorizeHttpRequests(auth ->
                 auth.requestMatchers("/user/adduser", "/user/login").permitAll() //je donne acce ken l login w signIn si nn ay requette lezem deja ykoun connceter
                         .anyRequest().authenticated()).build();
    }

    //maintenant il faut enregister mdp crypte
    @Bean
    public PasswordEncoder passwordEncoder(){
        return  new BCryptPasswordEncoder();
    }

    //un bean pour gerer l'auth , verifier mdp correspond ou nn
    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http, PasswordEncoder passwordEncoder) throws Exception {
        AuthenticationManagerBuilder authenticationManagerBuilder = http.getSharedObject(AuthenticationManagerBuilder.class);
        authenticationManagerBuilder.userDetailsService(userDetailsServices).passwordEncoder(passwordEncoder);
        return authenticationManagerBuilder.build();
    }

}
