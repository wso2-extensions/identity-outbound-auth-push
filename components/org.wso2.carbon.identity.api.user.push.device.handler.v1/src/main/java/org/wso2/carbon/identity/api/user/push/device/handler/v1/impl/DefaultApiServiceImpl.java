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
import org.wso2.carbon.identity.api.user.push.device.handler.v1.DefaultApiService;
import org.wso2.carbon.identity.api.user.push.device.handler.v1.core.PushDeviceHandlerService;
import org.wso2.carbon.identity.api.user.push.device.handler.v1.model.StatusDTO;

import javax.ws.rs.core.Response;

/**
 * Implementation class of Push device Handler Admin APIs .
 */
public class DefaultApiServiceImpl implements DefaultApiService {

    private static final Log log = LogFactory.getLog(DefaultApiServiceImpl.class);

    @Autowired
    private PushDeviceHandlerService deviceHandlerService;

    @Override
    public Response userIdPushAuthDevicesDeviceIdDelete(String userId, String deviceId) {

        if (log.isDebugEnabled()) {
            log.debug("Removing device : " + deviceId + " of User : " + userId + ".");
        }
        deviceHandlerService = new PushDeviceHandlerService();
        deviceHandlerService.unregisterDevice(deviceId);
        StatusDTO statusDTO = new StatusDTO();
        statusDTO.setOperation(PushDeviceApiConstants.OPERATION_REMOVE);
        statusDTO.setDeviceId(deviceId);
        statusDTO.setStatus(PushDeviceApiConstants.RESULT_SUCCESSFUL);
        return Response.ok().entity(statusDTO).build();
    }

    @Override
    public Response userIdPushAuthDevicesDeviceIdGet(String userId, String deviceId) {

        if (log.isDebugEnabled()) {
            log.debug("Fetching data of device : " + deviceId + " of user : " + userId + ".");
        }
        deviceHandlerService = new PushDeviceHandlerService();
        return Response.ok().entity(deviceHandlerService.getDevice(deviceId)).build();
    }

    @Override
    public Response userIdPushAuthDevicesGet(String userId) {

        if (log.isDebugEnabled()) {
            log.debug("Retrieving all devices of user : " + userId + ".");
        }
        deviceHandlerService = new PushDeviceHandlerService();
        return Response.ok().entity(deviceHandlerService.listDevices()).build();
    }
}
