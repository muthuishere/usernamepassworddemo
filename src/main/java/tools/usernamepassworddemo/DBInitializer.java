package tools.usernamepassworddemo;


import com.fasterxml.jackson.core.type.TypeReference;
        import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.util.StreamUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tools.usernamepassworddemo.users.User;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;


// An alternative to flyway or liquibase to initialize the database
@Component
@RequiredArgsConstructor
@Slf4j
public class DBInitializer {


    public static final String DEFAULT_TABLE_TO_BE_CHECKED = "users";
    public static final String USER_JSON_FILE_PATH = "/defaultusers.json" ;
    private final R2dbcEntityTemplate entityTemplate;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;


    private final DatabaseClient databaseClient;


    @PostConstruct
    public void initDatabase() throws Exception {

        // The below query is SQL compliant to be checked for a table to be exists

        databaseClient.sql("SELECT COUNT(*) FROM information_schema.tables WHERE table_name = '" + DEFAULT_TABLE_TO_BE_CHECKED + "'")
                .fetch()
                .one()
                .filter(result -> isCountZero(result))
                .flatMap(result -> insertSchema())
                .flatMap(result -> insertUsersData())
                .log()
                .subscribe();


    }

    private static Boolean isCountZero(Map<String, Object> rows) {
        Boolean result =false;
        Object countObject = rows.get("COUNT(*)");
        if(countObject != null){
            result =  (Long)countObject == 0;
        }
        return result;
    }

    private Mono<List<User>> insertUsersData() {
        try {
            ObjectMapper mapper = new ObjectMapper();
            TypeReference<List<User>> typeReference = new TypeReference<>() {};
            InputStream inputStream = TypeReference.class.getResourceAsStream(USER_JSON_FILE_PATH);
            List<User> users = mapper.readValue(inputStream, typeReference);
            log.info(DEFAULT_TABLE_TO_BE_CHECKED + ": {}", users);
            return Flux.fromIterable(users)
                    .map(user -> {
                        user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
                        return user;
                    })
                    .flatMap(user -> entityTemplate.insert(User.class).using(user)).collectList();

        } catch (Exception e) {
            System.out.println("Unable to read " + DEFAULT_TABLE_TO_BE_CHECKED + ": " + e.getMessage());
            return Mono.empty();

        }
    }

    private Mono<Long> insertSchema() {
        Resource resource = new ClassPathResource("schema.sql");
        String sql;
        try {
            sql = StreamUtils.copyToString(resource.getInputStream(), StandardCharsets.UTF_8);
            return databaseClient.sql(sql).fetch().rowsUpdated();
        } catch (Exception e) {
            return Mono.error(e);
        }
    }
}
