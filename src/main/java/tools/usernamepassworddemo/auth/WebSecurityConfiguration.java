package tools.usernamepassworddemo.auth;


import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.server.SecurityWebFilterChain;
import reactor.core.publisher.Mono;
import tools.usernamepassworddemo.shared.UserRole;

import java.util.stream.Stream;

import static org.springframework.security.config.Customizer.withDefaults;


@Slf4j
@AllArgsConstructor
@Configuration
public class WebSecurityConfiguration {



    private AuthenticationManager authenticationManager;
    private SecurityContextRepository securityContextRepository;

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }



    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {

        final String ADMIN = "admin";
        final String USER = "user";

        log.info("WebSecurityConfiguration.securitygWebFilterChain()");
        return http.exceptionHandling((exceptionHandling) ->
                exceptionHandling
                        // customize how to request for authentication
                        .authenticationEntryPoint((swe, e) ->
                                Mono.fromRunnable(() -> swe.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED))
                        ).accessDeniedHandler((swe, e) ->
                                Mono.fromRunnable(() -> swe.getResponse().setStatusCode(HttpStatus.FORBIDDEN))
                        )
        )
                .csrf(csrf->csrf.disable()).cors(cors->cors.disable())
                .formLogin(formLoginSpec -> formLoginSpec.disable())
                .authenticationManager(authenticationManager)
                .securityContextRepository(securityContextRepository)
                .authorizeExchange(exchanges ->


                        exchanges
                        .pathMatchers(
                                "/v2/api-docs",
                                "/v3/api-docs",
                                "/swagger-resources/**",
                                "/api/auth/**",
                                "actuator/**",
                                "/ws/**",
                                "/webjars/**").permitAll()
                                .pathMatchers("/api/users/**").authenticated()
                        .pathMatchers("/api/credits/**").hasAnyRole(AdminOrAbove())
                        .pathMatchers("/api/admin/**").hasAnyRole(AdminOrAbove())
                        .anyExchange().permitAll()
                ).build();
    }



    private String[] UserOrAbove() {
        return Stream.of(UserRole.values()).filter(userRole -> userRole.ordinal() >= UserRole.USER.ordinal()).map(UserRole::name).toArray(String[]::new);

    }
    private String[] AdminOrAbove() {
        return Stream.of(UserRole.values()).filter(userRole -> userRole.ordinal() >= UserRole.ADMIN.ordinal()).map(UserRole::name).toArray(String[]::new);

    }

}
