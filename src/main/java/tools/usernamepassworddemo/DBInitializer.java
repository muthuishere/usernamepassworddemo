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

@Component
@RequiredArgsConstructor
@Slf4j
public class DBInitializer {


    public static final String DEFAULTUSERS_JSON = "/defaultusers.json";
    private final R2dbcEntityTemplate entityTemplate;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;


    private final DatabaseClient databaseClient;


    @PostConstruct
    public void initUsers() throws Exception {


        databaseClient.sql("SELECT COUNT(*) FROM information_schema.tables WHERE table_name = 'users'")
                .fetch()
                .one()
                .filter(result -> {
                    Boolean returnVal =false;
                    Object o = result.get("COUNT(*)");
                    if(o != null){
                        returnVal =  (Long)o == 0;
                    }
                    return returnVal;
                })
                .flatMap(result -> {

                        Resource resource = new ClassPathResource("schema.sql");
                        String sql;
                        try {
                            sql = StreamUtils.copyToString(resource.getInputStream(), StandardCharsets.UTF_8);
                            return databaseClient.sql(sql).fetch().rowsUpdated();
                        } catch (Exception e) {
                            return Mono.error(e);
                        }


                }).flatMap(result -> {



                    try {
                        ObjectMapper mapper = new ObjectMapper();
                        TypeReference<List<User>> typeReference = new TypeReference<>() {};
                        InputStream inputStream = TypeReference.class.getResourceAsStream(DEFAULTUSERS_JSON);
                        List<User> users = mapper.readValue(inputStream, typeReference);
                        log.info("users: {}", users);
                        return Flux.fromIterable(users)
                                .map(user -> {

                                    log.info("user: {}", user);
                                    log.info("user: {}", user.getPassword());
                                    user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
                                    return user;
                                })
                                .flatMap(user -> entityTemplate.insert(User.class).using(user)).collectList();

                    } catch (Exception e) {
                        System.out.println("Unable to read users: " + e.getMessage());
                        return Mono.empty();

                    }




                }).log().subscribe();


//
//
//        entityTemplate.getDatabaseClient().sql("SELECT COUNT(*) FROM users")
//                .fetch()
//                .one()
//                .map(count -> (Long) count.get("COUNT(*)"))
//                .flatMapMany(count -> {
//                    if (count == 0) {
//                        // Read the JSON file and insert users
//                        ObjectMapper mapper = new ObjectMapper();
//                        TypeReference<List<User>> typeReference = new TypeReference<>() {};
//                        InputStream inputStream = TypeReference.class.getResourceAsStream(DEFAULTUSERS_JSON);
//                        try {
//                            List<User> users = mapper.readValue(inputStream, typeReference);
//                            return Flux.fromIterable(users)
//                                    .map(user -> {
//                                        user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
//                                        return user;
//                                    })
//                                    .flatMap(user -> entityTemplate.insert(User.class).using(user));
//
//                        } catch (Exception e) {
//                            System.out.println("Unable to read users: " + e.getMessage());
//                            return Mono.empty();
//                        }
//                    }
//                    return Mono.empty();
//                })
//                .log()
//                .subscribe();
    }
}
