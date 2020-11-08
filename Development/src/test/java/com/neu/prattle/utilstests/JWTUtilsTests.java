package com.neu.prattle.utilstests;

import com.neu.prattle.utils.JWTUtils;

import fse.team2.common.utils.Utils;
import org.junit.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.util.logging.Level;
import java.util.logging.Logger;

import io.jsonwebtoken.security.SignatureException;

import static org.junit.Assert.*;

public class JWTUtilsTests {
  private static final Logger logger = Logger.getLogger(JWTUtilsTests.class.getName());

  @Test
  public void testValidToken() {
    String userId = "user123";
    String webToken = JWTUtils.generateJWToken(userId);
    logger.log(Level.INFO, webToken);
    assertEquals(userId, JWTUtils.validateJWToken(webToken));
  }

  @Test
  public void testInvalidToken() {
    String userId = "user123";
    String webToken = JWTUtils.generateJWToken(userId);
    logger.log(Level.INFO, webToken);
    assertNull(JWTUtils.validateJWToken(webToken + "abc"));
  }

  @Test
  public void testPrivateConstructor() throws Exception {
    Constructor constructor = JWTUtils.class.getDeclaredConstructor();
    assertTrue(Modifier.isPrivate(constructor.getModifiers()));

    constructor.setAccessible(true);
    constructor.newInstance();
  }
}
