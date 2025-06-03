package tn.exemple.medicare.configs;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import static org.springframework.security.config.Customizer.withDefaults;
import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(securedEnabled = true)

public class SecurityConfig {
    private final JwtFilter jwtAuthFilter;
    private final UserDetailsServices userDetailsServices;
    private final AuthenticationProvider authenticationProvider;

    public SecurityConfig(@Lazy JwtFilter jwtAuthFilter,   @Lazy  UserDetailsServices userDetailsServices,   @Lazy  AuthenticationProvider authenticationProvider) {
        this.jwtAuthFilter = jwtAuthFilter;
        this.userDetailsServices = userDetailsServices;
        this.authenticationProvider = authenticationProvider;
    }

    @Bean
    //dans ce code bech naamil filter yaani ay requette avant de passer au controller il faut passer avec ce filtre
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
                .cors(withDefaults()) // on va utiliser dans Front
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth ->
                        auth.requestMatchers( "/user/adduser", "/user/login" , "/user/activate-account" , "/user/forgot-password" , "/user/reset-password", "/user/logout" , "/user/verify-code" , "/medication/search","/medication/pagination",("/ws/**"))
                                .permitAll() //je donne acce ken l login w signIn si nn ay requette lezem deja ykoun connceter auth/** tous les api qui concernet l'authentification
                                .requestMatchers("/admin/**").hasAuthority("ADMIN")
                                .anyRequest()
                                .authenticated())
                .sessionManagement(session -> session.sessionCreationPolicy(STATELESS))
                .authenticationProvider(authenticationProvider)
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
                ;
        return http.build();
    }


    @Bean
    public AuthenticationProvider authenticationProvider () {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider(); // nahna aamilna UserDetails f entite bech taayet interface mtaa UserDetails w yestaamil DAO
        authProvider.setUserDetailsService(userDetailsServices);
        authProvider.setPasswordEncoder(passwordEncoder());
        return  authProvider;
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
