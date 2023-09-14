package tools.usernamepassworddemo.auth;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class AuthenticationManager implements ReactiveAuthenticationManager {

   private final JwtService jwtService;

    @Override
    @SuppressWarnings("unchecked")
    public Mono<Authentication> authenticate(Authentication authentication) {
        String authToken = authentication.getCredentials().toString();
        TokenDetail detail = jwtService.decodeJWT(authToken);
        return Mono.just(detail)
                .switchIfEmpty(Mono.empty())
                .map(tokenDetail -> {

                    List<String> rolesMap = tokenDetail.getRoles();


                    log.info("Available roles" + rolesMap);
                    UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(
                            tokenDetail.getEmail(),
                            null,
                            rolesMap.stream().map(i -> "ROLE_" + i).map(SimpleGrantedAuthority::new).collect(Collectors.toList())
                    );
                    usernamePasswordAuthenticationToken.setDetails(tokenDetail);
                    return usernamePasswordAuthenticationToken;
                });
    }
}