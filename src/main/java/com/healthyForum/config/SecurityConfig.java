package com.healthyForum.config;

import com.healthyForum.config.CustomAuthenticationSuccessHandler;
import com.healthyForum.repository.UserRepository;
import com.healthyForum.repository.UserAccountRepository;
import com.healthyForum.model.User;
import com.healthyForum.model.UserAccount;
import com.healthyForum.service.oauth.CustomOAuth2UserService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import com.healthyForum.config.CustomAuthorizationRequestResolver;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizationRequestResolver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.authentication.LockedException;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserAccountRepository userAccountRepository;

    @Autowired
    private CustomOAuth2UserService customOAuth2UserService;

    @Autowired
    private CustomAuthenticationSuccessHandler customAuthenticationSuccessHandler;

    @Bean
    public AuthenticationFailureHandler customAuthenticationFailureHandler() {
        return (request, response, exception) -> {
            if (exception instanceof LockedException) {
                response.sendRedirect("/login?suspended=true");
            } else {
                response.sendRedirect("/login?error=true");
            }
        };
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(authz -> authz
                .requestMatchers("/css/**", "/js/**", "/images/**", "/assets/**", "/uploads/**").permitAll()
                .requestMatchers("/register", "/login", "/forgot-password", "/reset-password", "/verify").permitAll()
                .requestMatchers("/oauth2/**", "/login/oauth2/**").permitAll()
                .requestMatchers("/admin/**").hasRole("ADMIN")
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/login")
                .successHandler(customAuthenticationSuccessHandler)
                .failureHandler(customAuthenticationFailureHandler())
                .permitAll()
            )
            .oauth2Login(oauth2 -> oauth2
                .loginPage("/login")
                .userInfoEndpoint(userInfo -> userInfo
                    .userService(customOAuth2UserService)
                )
                .authorizationEndpoint(auth -> auth
                    .authorizationRequestResolver(
                        authorizationRequestResolver(
                            http.getSharedObject(ClientRegistrationRepository.class)
                        )
                    )
                )
                .successHandler(customAuthenticationSuccessHandler)
            )
            .logout(logout -> logout
                .logoutSuccessUrl("/login?logout=true")
                .permitAll()
            )
            .csrf(csrf -> csrf.disable());

        return http.build();
    }

    @Bean
    public OAuth2AuthorizationRequestResolver authorizationRequestResolver(
            ClientRegistrationRepository clientRegistrationRepository) {
        return new CustomAuthorizationRequestResolver(clientRegistrationRepository, "/oauth2/authorization");
    }

    @Bean
    @Transactional(readOnly = true)
    public UserDetailsService userDetailsService() {
        return new UserDetailsService() {
            @Override
            @Transactional(readOnly = true)
            public UserDetails loadUserByUsername(String usernameOrEmail) throws UsernameNotFoundException {
                // Try to find by username first
                UserAccount account = userAccountRepository.findByUsername(usernameOrEmail).orElse(null);
                if (account == null) {
                    // Try to find by email
                    User user = userRepository.findByEmail(usernameOrEmail).orElse(null);
                    if (user != null) {
                        account = userAccountRepository.findByUserId(user.getId()).orElse(null);
                    }
                }
                
                if (account == null) {
                    throw new UsernameNotFoundException("User not found: " + usernameOrEmail);
                }

                // Force loading of the user and role to avoid lazy loading issues
                User user = account.getUser();
                String roleName = user.getRole().getRoleName();

                return org.springframework.security.core.userdetails.User.builder()
                    .username(account.getUsername()) // Use username instead of email
                    .password(account.getPassword())
                    .disabled(!account.isEnabled())
                    .accountExpired(false)
                    .credentialsExpired(false)
                    .accountLocked(account.isSuspended())
                    .roles(roleName)
                    .build();
            }
        };
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // Only needed if you want to use AuthenticationManager elsewhere (optional)
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}
