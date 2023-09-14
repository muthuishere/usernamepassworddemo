package tools.usernamepassworddemo.shared;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

public class ResponseBuilder {


    public static <T> Mono<T> asError(HttpStatusCode status, String message, Throwable error) {

        return  Mono.error(new ResponseStatusException(status, message,error));
    }
    public static <T> Mono<T> asBadRequest(Throwable error) {

        return  asError(HttpStatus.BAD_REQUEST, error.getMessage(), error);
    }
    public static <T> Mono<T> asBadRequest(String reason,Throwable error) {

        return  asError(HttpStatus.BAD_REQUEST, reason, error);
    }
    public static <T> Mono<T> asBadRequest(String reason) {

        return  asError(HttpStatus.BAD_REQUEST, reason, new RuntimeException(reason));
    }
}
