package tools.usernamepassworddemo.users;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tools.usernamepassworddemo.auth.RegisterRequest;
import tools.usernamepassworddemo.shared.UserRole;

import java.security.Principal;
import static tools.usernamepassworddemo.shared.ResponseBuilder.asBadRequest;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin")
@Slf4j
public class UserAdminController {


    private final UserService userService;


    @GetMapping(path = "/all")
    public Flux<User> listAllUsers(Principal principal) {


        return userService.findAll();
    }

    @PostMapping("/registeradmin")
    public Mono<User> registerUser(@RequestBody RegisterRequest registerRequest) {
        try {


            return userService.registerAdmin(registerRequest.email(), registerRequest.password());

        } catch (Exception e) {
            e.printStackTrace();

            return Mono.error(new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage(),e));

        }
    }
    @PostMapping("/makeasadmin")
    public Mono<User> makeAsAdmin(@RequestBody Long idToken) {
        try {




            return userService.addRole(idToken, UserRole.ADMIN.getName());


        } catch (Exception e) {
            e.printStackTrace();
            return asBadRequest(e);

        }
    }
    @PostMapping("/removefromadmin")
    public Mono<User> removeFromAdmin(@RequestBody Long idToken) {
        try {


            return userService.removeRole(idToken, UserRole.ADMIN.getName());


        } catch (Exception e) {
            e.printStackTrace();
            return asBadRequest(e);

        }
    }
}
