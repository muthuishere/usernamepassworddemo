package tools.usernamepassworddemo.auth;

import org.assertj.core.util.Arrays;
import org.junit.jupiter.api.Test;
import tools.usernamepassworddemo.users.User;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class JwtServiceTest {

    @Test
    void generateJWT() {
        User user = new User();
        user.setId(1L);
        user.setEmail("test@example.com");
        List<String> user1 = new java.util.ArrayList<>(java.util.Arrays.asList("ADMIN"));
        user.setRoles(user1);

        JwtService jwtService = new JwtService();
        String token = jwtService.generateJWT(user);

        assertNotNull(token);
        assertFalse(token.isEmpty());
    }


}