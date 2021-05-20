package org.wso2.carbon.identity.application.authenticator.push.core.exception;

public class PushAuthJWTValidationException extends IdentityPushAuthException {

    public PushAuthJWTValidationException(String message) {

        super(message);
    }

    public PushAuthJWTValidationException(String message, Throwable e) {

        super(message, e);
    }
}
