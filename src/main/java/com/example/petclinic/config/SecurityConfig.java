package com.example.petclinic.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
            .csrf().disable() // For simplicity in example, disable CSRF
            .authorizeRequests()
            .antMatchers("/owners/by-user-id/**").hasAnyRole("ADMIN", "STAFF") // NFR-070: Secure new endpoint
            .antMatchers("/**").permitAll() // Allow all other requests for simplicity
            .and()
            .httpBasic(); // Use HTTP Basic authentication
    }

    @Bean
    @Override
    public UserDetailsService userDetailsService() {
        UserDetails user = User.withDefaultPasswordEncoder()
            .username("user")
            .password("password")
            .roles("USER")
            .build();

        UserDetails admin = User.withDefaultPasswordEncoder()
            .username("admin")
            .password("adminpass")
            .roles("ADMIN", "STAFF")
            .build();

        UserDetails staff = User.withDefaultPasswordEncoder()
            .username("staff")
            .password("staffpass")
            .roles("STAFF")
            .build();

        return new InMemoryUserDetailsManager(user, admin, staff);
    }
}