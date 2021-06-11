package org.wso2.carbon.identity.api.user.push.device.common;

import org.wso2.carbon.identity.application.authenticator.push.device.handler.DeviceHandler;

/**
 * Push API device handler service holder class.
 */
public class PushDeviceHandlerServiceHolder {

    private static DeviceHandler deviceHandler;

    public static void setPushDeviceHandlerService(DeviceHandler deviceHandler) {

        PushDeviceHandlerServiceHolder.deviceHandler = deviceHandler;
    }

    /**
     * Get TaskOperationService osgi service.
     *
     * @return TaskOperationService
     */
    public static DeviceHandler getPushDeviceHandlerService() {

        return deviceHandler;
    }

}
