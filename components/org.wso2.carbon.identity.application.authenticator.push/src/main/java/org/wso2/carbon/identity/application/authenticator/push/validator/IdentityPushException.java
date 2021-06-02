package org.wso2.carbon.identity.application.authenticator.push.validator;

public class IdentityPushException extends Exception {

    private static final long serialVersionUID = 1481059218426477598L; // TODO: Get the proper number

    public IdentityPushException(String message) {

        super(message);
    }

    public IdentityPushException(String message, Throwable e) {

        super(message, e);
    }
}
