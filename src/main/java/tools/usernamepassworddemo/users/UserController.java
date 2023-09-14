package tools.usernamepassworddemo.users;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;
import tools.usernamepassworddemo.auth.RegisterRequest;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
@Slf4j
public class UserController {



    private final UserService userService;

    @PostMapping("/register")
    public Mono<User> registerUser(@RequestBody RegisterRequest registerRequest) {
        try {


            return userService.registerUser(registerRequest.email(), registerRequest.password());

        } catch (Exception e) {
            e.printStackTrace();

            return Mono.error(new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage(),e));

        }
    }

    @GetMapping(path = "/get")
    public String test(Authentication authentication) {

        log.info("Authentication: " + authentication.toString());

        return authentication.getName(); //<-- returns Firebase user UID
    }

}
