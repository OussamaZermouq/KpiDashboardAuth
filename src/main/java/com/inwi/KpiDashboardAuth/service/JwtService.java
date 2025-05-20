package com.inwi.KpiDashboardAuth.service;

import com.inwi.KpiDashboardAuth.exceptions.NonValidTokenException;
import com.inwi.KpiDashboardAuth.model.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import java.util.function.Function;

@Service
public class JwtService {
    @Value("${secret.jwt-key}")
    private String secretKey;

    @Value("${security.jwt.expiration-time}")
    private Long jwtExpiration;

    public String extractUsername(String token){
        return extractClaim(token, Claims::getSubject);
    }

    private <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }


    public String generateTokenAccessToken(UserDetails userDetails) {
        return generateTokenAccessToken(new HashMap<>(), userDetails);
    }

    public String generateTokenAccessToken(Map<String, Object> extraClaims, UserDetails userDetails){
        return buildAccessToken(extraClaims, userDetails, jwtExpiration);
    }

    public long getExpirationTime(){
        return jwtExpiration;
    }

    private String buildToken(Map<String, Object> extraClaims, UserDetails userDetails, Long jwtExpiration){
        Map<String, String> type = new HashMap<>();

        type.put("tokenType","accessToken");
        return Jwts.
                builder().
                setClaims(extraClaims).
                setClaims(type).
                setSubject(userDetails.getUsername()).
                setIssuedAt(new Date(System.currentTimeMillis())).
                setExpiration(new Date(System.currentTimeMillis()+jwtExpiration)).
                signWith(getSignInKey(), SignatureAlgorithm.HS256).
                compact();
    }


    private String buildAccessToken(Map<String, Object> extraClaims, UserDetails userDetails, Long jwtExpiration){
        extraClaims.put("tokenType","accessToken");
        extraClaims.put("role",((User) userDetails).getRole().name());
        return Jwts.
                builder().
                setClaims(extraClaims).
                setSubject(userDetails.getUsername()).
                setIssuedAt(new Date(System.currentTimeMillis())).
                setExpiration(new Date(System.currentTimeMillis()+jwtExpiration)).
                signWith(getSignInKey(), SignatureAlgorithm.HS256).
                compact();
    }

    public boolean isTokenValid(String token, UserDetails userDetails){
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername())) && !isTokenExpired(token);
    }
    public String generateToken(UserDetails userDetails) {
        return generateToken(new HashMap<>(), userDetails);
    }

    public String generateToken(
            Map<String, Object> extraClaims,
            UserDetails userDetails
    ) {
        long jwtExpiration = 432000000L;
        return buildToken(extraClaims, userDetails, jwtExpiration);
    }
    public String generateRefreshToken(UserDetails userDetails){
        long refreshExpiration = 1209600000L;
        return buildRefreshToken(new HashMap<>(), userDetails, refreshExpiration);
    }

    public boolean isRefreshToken(String token) {
        try {
            Claims claims = extractAllClaims(token);
            return "refresh".equals(claims.get("tokenType"));
        } catch (Exception e) {
            return false;
        }
    }

    public String buildRefreshToken(Map<String, Object> extraClaims, UserDetails userDetails, long expiration){
        extraClaims.put("tokenType","refresh");
        return Jwts
                .builder()
                .setClaims(extraClaims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSignInKey(),SignatureAlgorithm.HS256)
                .compact();
    }

    private Boolean isTokenExpired(String token){
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token){
        return extractClaim(token, Claims::getExpiration);
    }

    private Claims extractAllClaims(String token){
        return Jwts.
                parserBuilder().
                setSigningKey(getSignInKey()).
                build().
                parseClaimsJws(token).
                getBody();
    }
    private Key getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
