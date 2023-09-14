package tools.usernamepassworddemo.auth;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static tools.usernamepassworddemo.shared.RoleParser.parseRolesFromString;

@Getter
@Setter
public class TokenDetail {

    Map<String, Object> claims = new HashMap<>();
    String email;



    public List<String> getRoles() {
        String rolesAsString = (String) claims.getOrDefault("roles", "");

        List<String>  roles = parseRolesFromString(rolesAsString);
        return roles;
    }

    public Long getUserId() {

        Long userId = (Long) claims.getOrDefault("id", -1L);

        // throw error
        return userId;
    }



}
