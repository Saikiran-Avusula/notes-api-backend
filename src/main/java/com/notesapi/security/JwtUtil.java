package com.notesapi.security;


import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;


import java.security.Key;
import java.util.Date;



//What each method does:
//
//generateToken(): Creates JWT with email as subject, sets expiry
//getEmailFromToken(): Extracts email from JWT (we use this to identify user)
//validateToken(): Checks if token is valid (not expired, not tampered)


@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secret;


    @Value("${jwt.expiration}")
    private Long expiration;

    private Key getSigningKey(){
        return Keys.hmacShaKeyFor(secret.getBytes());
    }


//    Generate JWT tokens
    public String generateToken(String email){
        Date now = new Date();
        Date expiryDate=new Date(now.getTime() + expiration);

        return Jwts.builder().setSubject(email)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(getSigningKey(), SignatureAlgorithm.HS512)
                .compact();
    }

//    get email from token
    public String getEmailFromToken(String token){
        Claims claims = Jwts.parser()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();

        return claims.getSubject();
    }

//    validation token
    public boolean validationToken(String token){
        try {
            Jwts.parser().setSigningKey(getSigningKey()).build().parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
           return false;
        }
    }

}
