package com.example.quanlytiemnet.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import com.example.quanlytiemnet.services.UserService;
import lombok.extern.slf4j.Slf4j;

@Configuration
@EnableWebSecurity
@Slf4j
public class SecurityConfig {

    @Autowired
    private UserService userService;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return NoOpPasswordEncoder.getInstance();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        log.info("Configuring DaoAuthenticationProvider");
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userService);
        provider.setPasswordEncoder(passwordEncoder());
        log.info("DaoAuthenticationProvider configured with userDetailsService: {}", userService.getClass().getName());
        return provider;
    }

    @Bean
    public AuthenticationManager authenticationManager(DaoAuthenticationProvider authenticationProvider) {
        log.info("Configuring AuthenticationManager with provider: {}", authenticationProvider.getClass().getName());
        return new ProviderManager(authenticationProvider);
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf
                        .ignoringRequestMatchers("/api/**")
                        .ignoringRequestMatchers("/auth/**")
                        .ignoringRequestMatchers("/admin/**"))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/auth/**", "/css/**", "/js/**", "/images/**", "/error").permitAll()
                        // Cho phép xem lịch làm việc
                        .requestMatchers(
                                "/admin/employees",
                                "/admin/employees/shifts/**",
                                "/admin/employees/shift-schedule/**")
                        .hasAnyRole("ADMIN", "EMPLOYEE")
                        // Chặn các thao tác CRUD
                        .requestMatchers(
                                "/admin/employees/add",
                                "/admin/employees/update",
                                "/admin/employees/delete/**",
                                "/admin/employees/edit/**")
                        .hasRole("ADMIN")
                        .requestMatchers("/admin/**").hasAnyRole("ADMIN", "EMPLOYEE")
                        .requestMatchers("/employee/**").hasRole("EMPLOYEE")
                        .requestMatchers("/member/**").hasRole("USER")
                        .anyRequest().authenticated())
                .formLogin(form -> form
                        .loginPage("/auth/login")
                        .loginProcessingUrl("/auth/login")
                        .defaultSuccessUrl("/default", true)
                        .failureUrl("/auth/login?error")
                        .permitAll())
                .logout(logout -> logout
                        .logoutUrl("/auth/logout")
                        .logoutSuccessUrl("/auth/login")
                        .permitAll());

        return http.build();
    }
}