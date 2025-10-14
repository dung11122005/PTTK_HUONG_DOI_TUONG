package com.example.exam_portal.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.web.cors.CorsConfigurationSource;

import com.example.exam_portal.service.CustomUserDetailsService;
import com.example.exam_portal.service.UploadService;
import com.example.exam_portal.service.UserService;
import com.example.exam_portal.service.userinfo.CustomOAuth2UserService;

import jakarta.servlet.DispatcherType;


@Configuration
@EnableMethodSecurity(securedEnabled = true)
public class SecurityConfiguration {

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private UserService userService;

    @Autowired
    private UploadService uploadService;

    @Autowired
    private CustomLogoutSuccessHandler customLogoutSuccessHandler;

    @Autowired
    private CorsConfigurationSource corsConfigurationSource;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public UserDetailsService userDetailsService(UserService userService) {
        return new CustomUserDetailsService(userService);
    }

    @Bean
    public AuthenticationProvider authenticationProvider(UserDetailsService userDetailsService,
                                                     PasswordEncoder passwordEncoder) {
            DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
            authProvider.setUserDetailsService(userDetailsService);
            authProvider.setPasswordEncoder(passwordEncoder);
        return authProvider;
    }




    @Bean
    public AuthenticationSuccessHandler customSuccessHandler() {
        return new CustomSuccessHandler(); // bạn đã định nghĩa class này
    }


    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .cors(cors -> cors.configurationSource(corsConfigurationSource))
            .csrf(csrf -> csrf
                .ignoringRequestMatchers("/api/**") // bỏ CSRF cho API
            )

            // ADMIN
            // PRINCIPAL
            // SUBJECT_DEPARTMENT
            // SUBJECT_TEACHER
            // STUDENT
            // HOMEROOM_TEACHER

            .authorizeHttpRequests(auth -> auth
                .dispatcherTypeMatchers(DispatcherType.FORWARD, DispatcherType.INCLUDE).permitAll()
                .requestMatchers("/","/courses/**", "/course/**", "/login/**", "/register", "/product/**", "/products/**",
                 "/css/**", "/js/**", "/img/**", "/fonts/**", "/uploads/**", "/api/v1/listresult/**",
                 "/api/chat/**", "/admin/**").permitAll()
                .anyRequest().authenticated()
            )

            .oauth2Login(oauth2 -> oauth2.loginPage("/login")
                        .successHandler(customSuccessHandler())
                        .failureUrl("/login?error")
                        .userInfoEndpoint(user -> user
                                .userService(new CustomOAuth2UserService(userService, uploadService))))

            .formLogin(form -> form
                .loginPage("/login")
                .successHandler(customSuccessHandler())
                .failureUrl("/login?error")
                .permitAll()
            )

            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessHandler(customLogoutSuccessHandler)
                .logoutSuccessUrl("/login?logout")
                .deleteCookies("JSESSIONID")
                .invalidateHttpSession(true)
            )

            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.ALWAYS)
                .invalidSessionUrl("/logout?expired")
                .maximumSessions(1)
                .maxSessionsPreventsLogin(false)
            )

            .rememberMe(remember -> remember
                .key("uniqueAndSecretKey")
                .rememberMeParameter("remember-me") // tùy tên checkbox
                .tokenValiditySeconds(10000)
                .userDetailsService(userDetailsService) // 👈 BẮT BUỘC
            )

            .exceptionHandling(ex -> ex.accessDeniedPage("/access-deny"));

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http,
                                                   AuthenticationProvider authenticationProvider) throws Exception {
    return http.getSharedObject(AuthenticationManagerBuilder.class)
               .authenticationProvider(authenticationProvider)
               .build();
}

}
