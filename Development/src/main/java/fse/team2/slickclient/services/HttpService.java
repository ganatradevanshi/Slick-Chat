package fse.team2.slickclient.services;

import java.util.Map;

import okhttp3.Response;

public interface HttpService {
  /**
   * Executes a GET request to the server.
   *
   * @param url     API endpoint where the request is to be made.
   * @param headers Headers to be set in the request as {@code Map>}.
   * @return response from the HTTP call as {@code Response}.
   */
  Response sendGET(String url, Map<String, String> headers);

  /**
   * Executes a POST request to the server.
   *
   * @param url        API endpoint where the request is to be made.
   * @param bodyParams data params to be sent in the request body as {@code Map}.
   * @param headers    Headers to be set in the request as {@code Map>}.
   * @return response from the HTTP call as {@code Response}
   */
  Response sendPOST(String url, Map<String, String> headers, Map<String, String> bodyParams);

  /**
   * Executes a PUT request to the server.
   *
   * @param url        API endpoint where the request is to be made.
   * @param bodyParams data params to be sent in the request body as {@code Map}.
   * @param headers    Headers to be set in the request as {@code Map>}.
   * @return response from the HTTP call as {@code Response}
   */
  Response sendPUT(String url, Map<String, String> headers, Map<String, String> bodyParams);

  /**
   * Executes a DELETE request to the server.
   *
   * @param url     API endpoint where the request is to be made.
   * @param headers Headers to be set in the request as {@code Map>}.
   * @return response from the HTTP call as {@code Response}
   */
  Response sendDELETE(String url, Map<String, String> headers);
}
