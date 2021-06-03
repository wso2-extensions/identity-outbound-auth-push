package org.wso2.carbon.identity.application.authenticator.push.dto;

/**
 * Interface for Authentication Data DTO.
 */
public interface AuthDataDTO {

    /**
     * Store the related challenge text for the session context.
     *
     * @param challenge Random challenge for authentication request
     */
    void setChallenge(String challenge);

    /**
     * Get the challenge text for the session context.
     *
     * @return Stored random challenge for authentication request
     */
    String getChallenge();

    /**
     * Store the JWT containing the authentication response data.
     *
     * @param authToken JWT containing information for the authentication request
     */
    void setAuthToken(String authToken);

    /**
     * Get the stored JWT containing the authentication response data.
     *
     * @return Stored authentication response token
     */
    String getAuthToken();
}
