package org.wso2.carbon.identity.application.authenticator.push.dto;

public interface AuthDataDTO {

    /**
     * Store the related challenge text for the session context
     *
     * @param challenge
     */
    void setChallenge(String challenge);

    /**
     * Get the challenge text for the session context
     *
     * @return
     */
    String getChallenge();

    /**
     * Store the JWT containing the authentication response data
     *
     * @param authToken
     */
    void setAuthToken(String authToken);

    /**
     * Get the stored JWT containing the authentication response data
     *
     * @return
     */
    String getAuthToken();
}
