package org.wso2.carbon.identity.sso.saml.javascript.flow;

/**
 * initializes getter setter methods for status and challenge in the temporary hashmap at the biometric endpoint.
 */
public class WaitStatusResponse {
    private String status;
    private String signedChallenge;



    public String getStatus() {

        return status;
    }

    public void setStatus(String status) {

        this.status = status;
    }

    public String getChallenge() {
        return  signedChallenge;

    }

    public void setChallenge(String challenge) {

        this.signedChallenge = challenge;
    }
}
