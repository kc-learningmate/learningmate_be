package org.kc5.learningmate.config;


import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.kc5.learningmate.domain.auth.filter.JwtAuthenticationFilter;
import org.kc5.learningmate.domain.auth.handler.MemberAuthenticationEntryPoint;
import org.kc5.learningmate.domain.auth.handler.Oauth2SuccessHandler;
import org.kc5.learningmate.domain.auth.properties.AuthProperties;
import org.kc5.learningmate.domain.auth.service.MemberOauth2Service;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@Log4j2
@EnableWebSecurity
@EnableConfigurationProperties({AuthProperties.class})
@RequiredArgsConstructor
public class MemberSecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final MemberOauth2Service memberOauth2Service;
    private final Oauth2SuccessHandler oauth2SuccessHandler;
    private final MemberAuthenticationEntryPoint memberAuthenticationEntryPoint;


    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, CorsConfigurationSource corsConfigurationSource) throws Exception {
        log.info("Security Filter Chain");

        http.formLogin(AbstractHttpConfigurer::disable)
            .httpBasic(AbstractHttpConfigurer::disable)
            .csrf(AbstractHttpConfigurer::disable)
            .cors(config -> config.configurationSource(corsConfigurationSource()))
            .sessionManagement(config -> config.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth ->
                    auth.requestMatchers("/api/v1/auth/**")
                        .permitAll()
                        .anyRequest()
                        .authenticated())
            .oauth2Login(oauth -> oauth.authorizationEndpoint(authorization -> authorization.baseUri("/api/v1/auth/login/oauth2"))
                                       .redirectionEndpoint(redirection -> redirection.baseUri("/api/v1/auth/login/oauth2/code/*"))
                                       .userInfoEndpoint(userInfo -> userInfo.userService(memberOauth2Service))
                                       .successHandler(oauth2SuccessHandler))
            .exceptionHandling(ex -> ex.authenticationEntryPoint(memberAuthenticationEntryPoint))
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    private CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration corsConfiguration = new CorsConfiguration();
        corsConfiguration.setAllowedOriginPatterns(List.of("*"));
        corsConfiguration.setAllowedMethods(List.of(
                HttpMethod.GET.name(),
                HttpMethod.POST.name(),
                HttpMethod.PATCH.name(),
                HttpMethod.DELETE.name(),
                HttpMethod.OPTIONS.name()));

        corsConfiguration.setAllowedHeaders(List.of(
                HttpHeaders.AUTHORIZATION,
                HttpHeaders.CACHE_CONTROL,
                HttpHeaders.CONTENT_TYPE));

        corsConfiguration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", corsConfiguration);

        return source;
    }
}
