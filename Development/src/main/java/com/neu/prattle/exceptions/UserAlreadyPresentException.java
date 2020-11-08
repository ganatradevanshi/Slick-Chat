package com.neu.prattle.exceptions;

import com.neu.prattle.service.dbservice.DatabaseService;
import fse.team2.common.models.mongomodels.UserModel;

/***
 * A representation of an error which is thrown where a request has been made
 * for creation of a user object that already exists in the system.
 * Refer {@link UserModel}
 * Refer {@link DatabaseService < UserModel >#add(User)}
 */
public class UserAlreadyPresentException extends RuntimeException {

    private static final long serialVersionUID = -4845176561270017896L;

    public UserAlreadyPresentException(String message) {
        super(message);
    }
}
