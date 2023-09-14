package tools.usernamepassworddemo.auth;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;
import tools.usernamepassworddemo.users.User;
import tools.usernamepassworddemo.users.UserService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
@Slf4j
public class AuthController {



    private final UserService userService;
    private final JwtService jwtService;

    @PostMapping("/register")
    public Mono<User> registerUser(@RequestBody RegisterRequest registerRequest) {
        try {


            return userService.registerUser(registerRequest.email(), registerRequest.password());

        } catch (Exception e) {
            e.printStackTrace();

            return Mono.error(new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage(),e));

        }
    }

    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public Mono<LoginResponse> login(@RequestBody LoginRequest loginRequest)  {

        try {

            return userService.validateUser(loginRequest.getEmail(),loginRequest.getPassword())
                    .map(user -> {
                        log.info("User: " + user.toString());
                        String token = jwtService.generateJWT(user);
                        LoginResponse result = new LoginResponse(loginRequest.getEmail(), token);
                        return result;
                    });



        } catch (IllegalArgumentException e) {

            return Mono.error(new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage(),e));
        }
    }


}
