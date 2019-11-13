package org.wso2.carbon.extension.identity.authenticator.dao;
/**
 * performs DAO operations related to Biometric device store.
 */
public interface BiometricDAO {


    void addDeviceID(String username, String deviceID);


    void updateDeviceID(String username, String deviceID);


    void deleteDeviceID(String username);


    String getDeviceID(String username);


}
