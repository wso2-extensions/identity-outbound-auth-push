package org.wso2.carbon.identity.application.authenticator.push.dto.impl;

import org.wso2.carbon.identity.application.authenticator.push.dto.AuthDataDTO;

import java.io.Serializable;

/**
 * DTO class for holding authentication data.
 */
public class AuthDataDTOImpl implements AuthDataDTO, Serializable {

    private static final long serialVersionUID = 5355319579322887235L;
    private String challenge;
    private String authToken;

    @Override
    public void setChallenge(String challenge) {

        this.challenge = challenge;
    }

    @Override
    public String getChallenge() {

        return this.challenge;
    }

    @Override
    public void setAuthToken(String authToken) {

        this.authToken = authToken;
    }

    @Override
    public String getAuthToken() {

        return this.authToken;
    }
}
