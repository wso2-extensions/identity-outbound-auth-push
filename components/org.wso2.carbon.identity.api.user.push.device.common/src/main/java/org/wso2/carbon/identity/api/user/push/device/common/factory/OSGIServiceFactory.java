/*licence*/

package org.wso2.carbon.identity.api.user.push.device.common.factory;

import org.springframework.beans.factory.config.AbstractFactoryBean;
import org.wso2.carbon.context.PrivilegedCarbonContext;
import org.wso2.carbon.identity.application.authenticator.push.device.handler.DeviceHandler;

/**
 * Factory Beans serves as a factory for creating other beans within the IOC container. This factory bean is used to
 * instantiate the TaskOperationService type of object inside the container.
 */
public class OSGIServiceFactory extends AbstractFactoryBean<DeviceHandler> {

    private DeviceHandler deviceHandler;

    @Override
    public Class<?> getObjectType() {
        return Object.class;
    }

    @Override
    protected DeviceHandler createInstance() throws Exception {

        if (this.deviceHandler == null) {
            DeviceHandler deviceHandler = (DeviceHandler) PrivilegedCarbonContext.
                    getThreadLocalCarbonContext().getOSGiService(DeviceHandler.class, null);
            if (deviceHandler != null) {
                this.deviceHandler = deviceHandler;
            } else {
                throw new Exception("Unable to retrieve PushDeviceHandlerService service.");
            }
        }
        return this.deviceHandler;
    }

}
