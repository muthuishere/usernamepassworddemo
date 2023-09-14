package tools.usernamepassworddemo.shared;

import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
public class RoleParser {

    public static List<String> parseRolesFromString(String rolesAsString1) {
        if(null == rolesAsString1 || rolesAsString1.isEmpty())
            return new ArrayList<>();

        return List.of(rolesAsString1.split(","));
    }



}
