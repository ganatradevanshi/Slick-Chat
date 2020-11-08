package fse.team2.slickclient.services;

import com.google.gson.Gson;

import java.io.IOException;
import java.util.Map;
import java.util.logging.Level;

import fse.team2.slickclient.constants.AppConstants;
import fse.team2.slickclient.utils.LoggerService;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class HttpServiceImpl implements HttpService {

  private static HttpServiceImpl singleInstance = null;
  private final OkHttpClient httpClient;

  private HttpServiceImpl() {
    this.httpClient = new OkHttpClient().newBuilder().addInterceptor(new InterceptorService()).build();
  }

  public static HttpService getInstance() {
    if (singleInstance == null) {
      singleInstance = new HttpServiceImpl();
    }
    return singleInstance;
  }

  @Override
  public Response sendGET(String url, Map<String, String> headers) {
    Request request = getRequestBuilder(url, headers).build();
    return execute(request);
  }

  @Override
  public Response sendPOST(String url, Map<String, String> headers, Map<String, String> bodyParams) {
    Request.Builder requestBuilder = getRequestBuilder(url, headers);
    RequestBody body = buildRequestBody(bodyParams);
    Request request = requestBuilder.post(body).build();
    return execute(request);
  }

  @Override
  public Response sendPUT(String url, Map<String, String> headers, Map<String, String> bodyParams) {
    Request.Builder requestBuilder = getRequestBuilder(url, headers);
    RequestBody body = buildRequestBody(bodyParams);
    Request request = requestBuilder.put(body).build();
    return execute(request);
  }

  @Override
  public Response sendDELETE(String url, Map<String, String> headers) {
    Request.Builder requestBuilder = getRequestBuilder(url, headers);
    Request request = requestBuilder.delete().build();
    return execute(request);
  }

  /**
   * Get a request builder object initialized with specified url and headers.
   *
   * @param url     url to set in the request builder.
   * @param headers headers to be set in the request builder.
   * @return reference to request builder as {@code Request.Builder}.
   */
  private Request.Builder getRequestBuilder(String url, Map<String, String> headers) {
    Request.Builder requestBuilder = new Request.Builder().url(url);
    if (headers != null) {
      headers.forEach(requestBuilder::addHeader);
    }
    return requestBuilder;
  }

  /**
   * Converts a map with body params into json and creates a RequestBody with the resulting json.
   *
   * @param bodyParams body params as {@code Map}.
   * @return request body with json data as {@code RequestBody}.
   */
  private RequestBody buildRequestBody(Map<String, String> bodyParams) {
    Gson gson = new Gson();
    return RequestBody.create(gson.toJson(bodyParams), AppConstants.JSON);
  }

  /**
   * Executes the request on the server.
   *
   * @param request request object to be sent, consisting of url, method, body params and headers.
   * @return response from the server as a result of the http call as {@code Response}.
   */
  private Response execute(Request request) {
    Response response;
    try {
      response = httpClient.newCall(request).execute();
      if (!response.isSuccessful()) throw new IOException("Unexpected response " + response);
      return response;
    } catch (IOException e) {
      LoggerService.log(Level.SEVERE, e.getMessage());
    }
    return null;
  }
}
