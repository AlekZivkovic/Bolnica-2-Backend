package com.raf.si.userservice.utils;

import com.raf.si.userservice.model.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@AllArgsConstructor
public class JwtUtil {
    private final String SECRET_KEY;

    public Claims extractAllClaims(String token) {
        return Jwts.parser().setSigningKey(SECRET_KEY).parseClaimsJws(token).getBody();
    }

    public String extractUsername(String token) {
        return extractAllClaims(token).getSubject();
    }

    public boolean isTokenExpired(String token) {
        return extractAllClaims(token).getExpiration().before(new Date());
    }

    public String generateToken(User user) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("name",user.getFirstName());
        claims.put("last_name",user.getLastName());
        claims.put("title",user.getTitle());
        claims.put("profession",user.getProfession());
        claims.put("lbz",user.getLbz());
        claims.put("pbo",user.getDepartment().getPbo());
        claims.put("department_name",user.getDepartment().getName());
        claims.put("pbb",user.getDepartment().getHospital().getPbb());
        claims.put("hospital_name",user.getDepartment().getHospital().getFullName());
        claims.put("permissions",user.getPermissions());

        return Jwts.builder()
                .setSubject(user.getUsername())
                .setClaims(claims)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 10))
                .signWith(SignatureAlgorithm.HS512, SECRET_KEY).compact();
    }

    public boolean validateToken(String token, UserDetails user) {
        return (user.getUsername().equals(extractUsername(token)) && !isTokenExpired(token));
    }
}
