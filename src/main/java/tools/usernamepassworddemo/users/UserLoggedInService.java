package tools.usernamepassworddemo.users;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import tools.usernamepassworddemo.auth.TokenDetail;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserLoggedInService {


    private final UserService userService;
    public Mono<User> getLoggedInUserWithDetails() {
        return getLoggedInTokenDetail()
                .map(TokenDetail::getUserId)
                .flatMap(userService::findById);
    }
    public Mono<TokenDetail> getLoggedInTokenDetail() {

        return ReactiveSecurityContextHolder.getContext()
                .map(securityContext -> securityContext.getAuthentication())
                .filter(authentication -> authentication != null)
                .map(authentication -> authentication.getDetails())
                .filter(details -> details != null)
                .filter(details -> details instanceof TokenDetail)
                .map(details -> (TokenDetail) details);
    }
}
