package tools.usernamepassworddemo.auth;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.stereotype.Component;
import tools.usernamepassworddemo.users.User;

import java.util.Date;
import java.util.Map;

@Component
public class JwtService {
    String secret = "whyshouldineedthiskeyandwhatisexpectedoutofitand"; // dont use hyphens in secret key , it will throw error
    public static final  long JWT_TOKEN_VALIDITY = 12 * 60 * 60;

    public String generateJWT(User user) {


        String token = Jwts.builder()
                .setSubject(user.getEmail())
                .claim("roles", user.getRolesAsString())  // Add role as a claim
                .claim("id", user.getId())  // Add id as a claim
                .signWith(SignatureAlgorithm.HS256, secret)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + JWT_TOKEN_VALIDITY*1000))
                .compact();

        return token;
    }

    public TokenDetail decodeJWT(String token) {


        Claims claims = Jwts.parser()
                .setSigningKey(secret)
                .parseClaimsJws(token)
                .getBody();

        TokenDetail tokenDetail = new TokenDetail();
        tokenDetail.setClaims(claims);

        tokenDetail.setEmail(claims.getSubject());


        return tokenDetail;
    }

}
