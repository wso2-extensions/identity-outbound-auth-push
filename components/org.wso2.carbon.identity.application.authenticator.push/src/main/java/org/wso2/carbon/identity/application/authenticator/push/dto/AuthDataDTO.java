package org.wso2.carbon.identity.application.authenticator.push.dto;

import java.io.Serializable;

/**
 * DTO class for holding authentication data.
 */
public class AuthDataDTO implements Serializable {

    private static final long serialVersionUID = 5355319579322887235L;
    private String challenge;
    private String authToken;

    public void setChallenge(String challenge) {

        this.challenge = challenge;
    }

    public String getChallenge() {

        return this.challenge;
    }

    public void setAuthToken(String authToken) {

        this.authToken = authToken;
    }

    public String getAuthToken() {

        return this.authToken;
    }
}
