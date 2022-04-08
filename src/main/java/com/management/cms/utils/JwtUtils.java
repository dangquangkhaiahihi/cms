package com.management.cms.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.management.cms.model.response.ResponseAuthJwt;
import io.jsonwebtoken.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class JwtUtils {
  private static final Logger logger = LoggerFactory.getLogger(JwtUtils.class);
  public final Gson gson = new GsonBuilder().disableHtmlEscaping().create();

  @Value("${spring.security.jwt.jwtSecret}")
  private String secretKey;

  @Value("${spring.security.jwt.jwtExpirationMs}")
  private Long expireTime;

  public String generateToken(ResponseAuthJwt responseAuthJwt) {
    String strResponseAuthJwt = gson.toJson(responseAuthJwt);

    return Jwts.builder()
        .setSubject(strResponseAuthJwt)
        .setIssuedAt(new Date())
        .setExpiration(new Date((new Date()).getTime() + expireTime))
        .signWith(SignatureAlgorithm.HS512, secretKey)
        .compact();
  }

  public ResponseAuthJwt getInfoAuthFromJwtToken(String token) {
    String strAuthJwtDto = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody().getSubject();
    ResponseAuthJwt responseAuthJwt = gson.fromJson(strAuthJwtDto, ResponseAuthJwt.class);
    return responseAuthJwt;
  }

  public boolean validateJwtToken(String authToken) throws Exception {
    try {
      Jwts.parser().setSigningKey(secretKey).parseClaimsJws(authToken);
      return true;
    } catch (SignatureException e) {
      logger.error("Invalid JWT signature -> Message", e);
      throw new Exception(e.getMessage());
    } catch (MalformedJwtException e) {
      logger.error("Invalid JWT token -> Message", e);
      throw new Exception(e.getMessage());
    } catch (ExpiredJwtException e) {
      logger.error("Expired JWT token -> Message", e);
      throw new Exception(e.getMessage());
    } catch (UnsupportedJwtException e) {
      logger.error("Unsupported JWT token -> Message", e);
      throw new Exception(e.getMessage());
    } catch (IllegalArgumentException e) {
      logger.error("JWT claims string is empty -> Message", e);
      throw new Exception(e.getMessage());
    }
  }
}
