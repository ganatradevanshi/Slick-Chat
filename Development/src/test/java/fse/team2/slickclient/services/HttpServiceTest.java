package fse.team2.slickclient.services;

import org.apache.http.HttpStatus;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;

import fse.team2.slickclient.services.HttpService;
import fse.team2.slickclient.services.HttpServiceImpl;

import static org.junit.Assert.assertEquals;


public class HttpServiceTest {
  private HttpService httpService;
  private String testURL;

  @Before
  public void setUp() {
    this.httpService = HttpServiceImpl.getInstance();
    this.testURL = "http://postman-echo.com";
  }

  @Test
  public void testSendGET() {
    assertEquals(HttpStatus.SC_OK, this.httpService.sendGET(this.testURL + "/get", new HashMap<>()).code());
  }

  @Test
  public void testSendPOST() {
    assertEquals(HttpStatus.SC_OK, this.httpService.sendPOST(this.testURL + "/post", new HashMap<>(), new HashMap<>()).code());
  }

  @Test
  public void testSendPUT() {
    assertEquals(HttpStatus.SC_OK, this.httpService.sendPUT(this.testURL + "/put", new HashMap<>(), new HashMap<>()).code());
  }

  @Test
  public void testSendDELETE() {
    assertEquals(HttpStatus.SC_OK, this.httpService.sendDELETE(this.testURL + "/delete", new HashMap<>()).code());
  }
}
