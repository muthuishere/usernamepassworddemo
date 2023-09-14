package tools.usernamepassworddemo.users;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tools.usernamepassworddemo.shared.UserRole;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {



    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;





    public Mono<User> findByEmail(String s) {
        return userRepository.findByEmail(s);
    }

    public Mono<User> findById(Long userId) {
        return userRepository.findById(userId);
    }

    public Mono<User> registerUser(String email, String password) {


        User newUser = User.buildNormalUser(email, bCryptPasswordEncoder.encode(password));
        log.info("Registering user {}", email);
        return userRepository.save(newUser);
    }
    public Mono<User> registerAdmin(String email, String password) {


        User newUser = User.buildAdminUser(email, bCryptPasswordEncoder.encode(password));
        log.info("Registering user {}", email);
        return userRepository.save(newUser);
    }

    public Mono<User> addRole(Long idToken,String key) {

        if(UserRole.getRoleByName(key) == null)
            return Mono.error(new IllegalArgumentException("Invalid Role"));

        return findById(idToken).flatMap(user -> {

            List<String> roles = user.getRoles();

            if(!roles.contains(key))
                roles.add(key);

            user.setRoles(roles);
            return userRepository.save(user);
        });

    }
    public Mono<User> removeRole(Long idToken,String key) {

        if(UserRole.getRoleByName(key) == null)
            return Mono.error(new IllegalArgumentException("Invalid Role"));

        return findById(idToken).flatMap(user -> {
            List<String> roles = user.getRoles();

            if(roles.contains(key))
                roles.remove(key);


            user.setRoles(roles);
            return userRepository.save(user);
        });

    }

    public Flux<User> findAll() {
        return userRepository.findAll();
    }

    public Mono<User> validateUser(String email, String password) {

      return  userRepository.findByEmail(email)
                .filter(u -> u.getEmail().equals(email) && bCryptPasswordEncoder.matches(password, u.getPassword()))
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Invalid Credentials")));

    }
}
