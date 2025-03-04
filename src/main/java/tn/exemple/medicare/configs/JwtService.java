package tn.exemple.medicare.configs;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component

public class JwtService { // service responsable a gener tokne , decode , extract info from token validate token ect kol chy tebaa token
    @Value("${jwt.secret-key}")
    private  String secretKey;

    @Value("${jwt.expiration}")
    private  long jwtExpiration;

    @Value("${jwt.refresh-token.expiration}")
    private  long refreshExpiration;
    public String extractUsername(String token) {
        return  extractClaim(token, Claims::getSubject);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimResolver) {
        final  Claims claims = extractAllClaims(token);
        return claimResolver.apply(claims);
    }
    private Claims extractAllClaims(String token) {
        return  Jwts
                .parserBuilder()
                .setSigningKey(getSignInKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
    public  String generateToken(UserDetails userDetails){
        return  generateToken(new HashMap<>(), userDetails);
    }

    public String generateToken(HashMap<String,Object> claims, UserDetails userDetails) {

        return buildToken(claims, userDetails , jwtExpiration);
    }
    public String generateRefreshToken(UserDetails userDetails) {

        return buildToken(new HashMap<>(), userDetails ,refreshExpiration);
    }
    public long getRefreshTokenExpiration() {
        return refreshExpiration;
    }
    private String buildToken
            (HashMap<String, Object> extraClaims,
             UserDetails userDetails, long
                     expiration) {
        var authorities = userDetails.getAuthorities()
                .stream() //list
                .map(GrantedAuthority::getAuthority)
                .toList();
           return Jwts
                   .builder().setClaims(extraClaims)
                   .setSubject(userDetails.getUsername())
                   .setIssuedAt(new Date(System.currentTimeMillis()))
                   .setExpiration(new Date(System.currentTimeMillis()+ expiration ))
                   .claim("authorities" , authorities)
                   .signWith(getSignInKey())
                   .compact();
    }

    public boolean isTokenValid(String token ,UserDetails userDetails){
        final  String username = extractUsername(token);
        return (username.equals(userDetails.getUsername())) && !isTokenExpired(token);
    }

    private boolean isTokenExpired(String token) {
        return  extractExpiration(token).before(new  Date());
    }

    private Date extractExpiration(String token) {
        return  extractClaim(token, Claims ::getExpiration);
    }


    private Key getSignInKey() { //decoder notre key pour l'utiliser dans donction en haut
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }

}
