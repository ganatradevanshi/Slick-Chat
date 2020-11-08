package com.neu.prattle.utils;

import java.security.Key;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

public class JWTUtils {
  private static final Key key = Keys.secretKeyFor(SignatureAlgorithm.HS256);

  private JWTUtils(){

  }

  public static String generateJWToken(String userId) {
    return Jwts.builder().setSubject(userId).signWith(key).compact();
  }

  public static String validateJWToken(String token) {
    try {
      return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody().getSubject();
    } catch (Exception e) {
      return null;
    }
  }
}
