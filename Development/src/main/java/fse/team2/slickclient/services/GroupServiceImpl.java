package fse.team2.slickclient.services;

import fse.team2.slickclient.constants.AppConstants;
import okhttp3.Response;
import org.apache.http.HttpStatus;

import java.util.HashMap;
import java.util.Map;

public class GroupServiceImpl implements GroupService {

  private static GroupServiceImpl singleInstance = null;
  private HttpService httpService;
  private Map<String, String> headers;
  private Map<String, String> bodyParams;
  private static final String GROUP_ID_URL_PARAM = "{groupId}";
  private static final String USER_ID_URL_PARAM = "{userId}";

  private GroupServiceImpl(HttpService httpService) {
    this.httpService = httpService;
  }

  public static GroupServiceImpl getInstance(HttpService httpService) {
    if(singleInstance == null){
      singleInstance = new GroupServiceImpl(httpService);
    }
    return singleInstance;
  }

  public static GroupServiceImpl getInstanceNonSingleton(HttpService httpService) {
    return new GroupServiceImpl(httpService);
  }

  @Override
  public boolean createGroup(String name) {
    this.bodyParams = new HashMap<>();
    Response response = httpService.sendPOST(AppConstants.HOSTNAME + AppConstants.CREATE_GROUP_ENDPOINT.replace("{name}", name), headers, this.bodyParams);
    if (response == null || response.code() == HttpStatus.SC_FORBIDDEN || response.code() == HttpStatus.SC_UNAUTHORIZED) {
      return false;
    }
    return response.code() == HttpStatus.SC_OK;
  }

  @Override
  public boolean deleteGroup(String groupId) {
    Response response = httpService.sendDELETE(AppConstants.HOSTNAME + AppConstants.DELETE_GROUP_ENDPOINT.replace(GROUP_ID_URL_PARAM, groupId), headers);
    if (response == null || response.code() == HttpStatus.SC_FORBIDDEN || response.code() == HttpStatus.SC_UNAUTHORIZED) {
      return false;
    }
    return response.code() == HttpStatus.SC_OK;
  }

  @Override
  public boolean addUserToGroup(String groupId, String userId) {
    this.bodyParams = new HashMap<>();
    Response response = httpService.sendPOST(AppConstants.HOSTNAME + AppConstants.ADD_USER_TO_GROUP_ENDPOINT.replace(GROUP_ID_URL_PARAM, groupId).replace(USER_ID_URL_PARAM, userId), headers, bodyParams);
    if (response == null || response.code() == HttpStatus.SC_FORBIDDEN || response.code() == HttpStatus.SC_UNAUTHORIZED) {
      return false;
    }
    return response.code() == HttpStatus.SC_OK;
  }

  @Override
  public boolean removeUserFromGroup(String groupId, String userId) {
    this.bodyParams = new HashMap<>();
    Response response = httpService.sendPOST(AppConstants.HOSTNAME + AppConstants.REMOVE_USER_FROM_GROUP_ENDPOINT.replace(GROUP_ID_URL_PARAM, groupId).replace(USER_ID_URL_PARAM, userId), headers, bodyParams);
    if (response == null || response.code() == HttpStatus.SC_FORBIDDEN || response.code() == HttpStatus.SC_UNAUTHORIZED) {
      return false;
    }
    return response.code() == HttpStatus.SC_OK;
  }

  @Override
  public boolean addModerator(String groupId, String userId) {
    this.bodyParams = new HashMap<>();
    Response response = httpService.sendPOST(AppConstants.HOSTNAME + AppConstants.ASSIGN_MODERATOR_TO_GROUP_ENDPOINT.replace(GROUP_ID_URL_PARAM, groupId).replace(USER_ID_URL_PARAM, userId), headers, bodyParams);
    if (response == null || response.code() == HttpStatus.SC_FORBIDDEN || response.code() == HttpStatus.SC_UNAUTHORIZED) {
      return false;
    }
    return response.code() == HttpStatus.SC_OK;
  }
}
