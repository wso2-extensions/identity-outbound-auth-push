package org.wso2.carbon.identity.api.user.push.device.common;

//import org.wso2.carbon.identity.api.user.push.device.handler.v1.core.PushDeviceHandlerService;
import org.wso2.carbon.identity.application.authenticator.push.device.handler.DeviceHandler;

/**
 *
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
