package gov.nci.ppe.configurations;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@Profile("!local-no-auth")
public class WebSecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .cors().and()
            .csrf().disable()
            .authorizeHttpRequests(auth -> auth
                .antMatchers("/", "/publicapi/**", "/swagger-ui/**", "/v3/api-docs/**").permitAll()
                .antMatchers(HttpMethod.POST, "/api/v1/login").permitAll()
                .antMatchers(HttpMethod.POST, "/privateapi/v1/user/insert-open-data").permitAll()
                .antMatchers(HttpMethod.POST, "/privateapi/v1/send-reminder").permitAll()
                .anyRequest().authenticated()
            )
            .oauth2ResourceServer(oauth2 -> oauth2.jwt());
        return http.build();
    }
}
