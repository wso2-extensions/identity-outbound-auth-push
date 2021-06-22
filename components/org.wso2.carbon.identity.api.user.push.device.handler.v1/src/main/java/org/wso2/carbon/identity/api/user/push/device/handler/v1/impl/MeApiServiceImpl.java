/*
 * Copyright (c) 2020, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 *
 */

package org.wso2.carbon.identity.api.user.push.device.handler.v1.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.wso2.carbon.identity.api.user.push.device.common.util.PushDeviceApiConstants;
import org.wso2.carbon.identity.api.user.push.device.handler.v1.MeApiService;
import org.wso2.carbon.identity.api.user.push.device.handler.v1.core.PushDeviceHandlerService;
import org.wso2.carbon.identity.api.user.push.device.handler.v1.model.PatchDTO;
import org.wso2.carbon.identity.api.user.push.device.handler.v1.model.RegistrationRequestDTO;
import org.wso2.carbon.identity.api.user.push.device.handler.v1.model.RemoveRequestDTO;

import java.text.MessageFormat;
import java.util.List;
import javax.ws.rs.core.Response;

import static org.wso2.carbon.identity.api.user.common.ContextLoader.buildURIForHeader;

/**
 * Implementation class of Push device Handler User APIs.
 */
public class MeApiServiceImpl implements MeApiService {

    private static final Log log = LogFactory.getLog(MeApiServiceImpl.class);

    @Autowired
    private PushDeviceHandlerService pushDeviceHandlerService;

    @Override
    public Response mePushAuthDevicesDeviceIdDelete(String deviceId) {

        if (log.isDebugEnabled()) {
            log.debug(MessageFormat.format("Removing device : {0} ", deviceId));
        }
        pushDeviceHandlerService.unregisterDevice(deviceId);
        return Response.noContent().build();
    }

    @Override
    public Response mePushAuthDevicesDeviceIdGet(String deviceId) {

        if (log.isDebugEnabled()) {
            log.debug(MessageFormat.format("Fetching data of device : {0}", deviceId));
        }
        return Response.ok().entity(pushDeviceHandlerService.getDevice(deviceId)).build();
    }

    @Override
    public Response mePushAuthDevicesDeviceIdPatch(String deviceId, List<PatchDTO> patchDTO) {

        pushDeviceHandlerService.editDevice(deviceId, patchDTO);
        return Response.ok().build();
    }

    @Override
    public Response mePushAuthDevicesDeviceIdRemovePost(String deviceId, RemoveRequestDTO removeRequestDTO) {

        String token = removeRequestDTO.getToken();
        pushDeviceHandlerService.unregisterDeviceMobile(deviceId, token);

        return Response.noContent().build();
    }

    @Override
    public Response mePushAuthDevicesGet() {

        return Response.ok().entity(pushDeviceHandlerService.listDevices()).build();
    }

    @Override
    public Response mePushAuthDevicesPost(RegistrationRequestDTO registrationRequest) {

        if (log.isDebugEnabled() && registrationRequest != null) {
            log.debug("Received registration request from mobile device: "
                    + registrationRequest.getDeviceId() + ".");
        }
        if (registrationRequest != null) {
            pushDeviceHandlerService.registerDevice(registrationRequest);

            String registeredDevicePath = String.format(PushDeviceApiConstants.V1_API_PATH_COMPONENT
                            + PushDeviceApiConstants.PUSH_AUTH_GET_DEVICE_PATH, registrationRequest.getDeviceId());

            return Response.accepted(buildURIForHeader(registeredDevicePath)).build();
        } else {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
    }

    @Override
    public Response mePushAuthDiscoveryDataGet() {

        return Response.ok().entity(pushDeviceHandlerService.getDiscoveryData()).build();
    }
}
