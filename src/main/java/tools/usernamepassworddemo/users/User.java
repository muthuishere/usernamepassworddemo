package tools.usernamepassworddemo.users;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Table;
import tools.usernamepassworddemo.shared.UserRole;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static tools.usernamepassworddemo.shared.Constants.USER_TYPE;
import static tools.usernamepassworddemo.shared.RoleParser.parseRolesFromString;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table("users")
public class User  {



    @Id
    Long id;


    private String email;


    @ToString.Exclude
    private String password;

   String rolesAsString;


    @Transient
    @Builder.Default
  List<String> roles = new ArrayList<>();


    public void setRoles(List<String> roles){
        this.rolesAsString = roles.stream().collect(Collectors.joining(","  ));
    }

    public List<String> getRoles() {
        String rolesAsString1 = this.rolesAsString;
        return parseRolesFromString(rolesAsString1);
    }



    public static  User buildNormalUser(String email, String password){
        List<String> roles = new ArrayList<>();
        roles.add( UserRole.USER.getName());
        return User.builder()
                .email(email)
                .password(password)
                .roles(roles)
                .build();
    }

    public static  User buildAdminUser(String email, String password){
        List<String> roles = new ArrayList<>();
        roles.add( UserRole.ADMIN.getName());
        return User.builder()
                .email(email)
                .password(password)
                .roles(roles)
                .build();
    }

    @JsonProperty("password")
    public void setPassword(String password) {
        this.password = password;
    }

    // Getter to mask the password during serialization
    @JsonIgnore
    public String getPassword() {
        return password;
    }

}
