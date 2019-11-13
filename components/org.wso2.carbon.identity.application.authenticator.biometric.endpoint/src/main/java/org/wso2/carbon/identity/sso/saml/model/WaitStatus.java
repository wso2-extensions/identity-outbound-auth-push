package org.wso2.carbon.identity.sso.saml.model;
import java.io.Serializable;

/**
 * initializes getter setter methods for statuses in the temporary hashmap at the biometric endpoint.
 */
public class WaitStatus implements Serializable {
    /**
     * initializes the types of statuses in the temporary hashmap at the biometric endpoint.
     */
    public enum Status {
        WAITING, COMPLETED1, UNKNOWN
    }

    private Status status;

    public Status getStatus() {

        return status;
    }

    public void setStatus(Status status) {

        this.status = status;
    }
}
