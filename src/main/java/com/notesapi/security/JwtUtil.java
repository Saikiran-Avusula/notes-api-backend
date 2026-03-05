package com.notesapi.security;


import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;


import java.security.Key;
import java.util.Date;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;



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
        // Ensure the key is at least 512 bits (64 bytes) for HS512
        byte[] keyBytes = secret.getBytes(StandardCharsets.UTF_8);
        // Pad the key if it's too short
        if (keyBytes.length < 64) {
            byte[] paddedKey = new byte[64];
            System.arraycopy(keyBytes, 0, paddedKey, 0, keyBytes.length);
            // Fill remaining bytes with the secret repeated
            for (int i = keyBytes.length; i < 64; i++) {
                paddedKey[i] = keyBytes[i % keyBytes.length];
            }
            keyBytes = paddedKey;
        }
        return new SecretKeySpec(keyBytes, SignatureAlgorithm.HS512.getJcaName());
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
