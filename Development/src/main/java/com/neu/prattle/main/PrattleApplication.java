package com.neu.prattle.main;

import com.neu.prattle.controller.CorsFilter;
import com.neu.prattle.controller.GovernmentRegulationController;
import com.neu.prattle.controller.GroupController;
import com.neu.prattle.controller.MessageController;
import com.neu.prattle.controller.UserController;

import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.core.Application;

/***
 * Sets up the resource classes for handling REST requests.
 * Refer {@link Application}
 */
public class PrattleApplication extends Application {
  private Set<Class<?>> resourceClasses = new HashSet<>();

  @Override
  public Set<Class<?>> getClasses() {
    resourceClasses.add(UserController.class);
    resourceClasses.add(MessageController.class);
    resourceClasses.add(GroupController.class);
    resourceClasses.add(GovernmentRegulationController.class);
    resourceClasses.add(CorsFilter.class);
    return resourceClasses;
  }
}
