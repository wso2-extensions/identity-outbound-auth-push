/*
* Copyright (c) 2020, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
* http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/

package org.wso2.carbon.identity.api.user.push.device.handler.v1;

import org.springframework.beans.factory.annotation.Autowired;
import org.apache.cxf.jaxrs.ext.multipart.Attachment;
import org.apache.cxf.jaxrs.ext.multipart.Multipart;
import java.io.InputStream;
import java.util.List;

import org.wso2.carbon.identity.api.user.push.device.handler.v1.model.DeviceDTO;
import org.wso2.carbon.identity.api.user.push.device.handler.v1.model.ErrorDTO;
import org.wso2.carbon.identity.api.user.push.device.handler.v1.DefaultApiService;

import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import io.swagger.annotations.*;

import javax.validation.constraints.*;

@Path("/")
@Api(description = "The  API")

public class DefaultApi  {

    @Autowired
    private DefaultApiService delegate;

    @Valid
    @DELETE
    @Path("/{userId}/push-auth/devices/{deviceId}")

    @Produces({ "application/json" })
    @ApiOperation(value = "Remove a device by deviceId. ", notes = "This API is used by an admin to remove a registered device by the deviceId.<br/> <b>Permission required:</b> <br>   * /permission/admin/manage/identity/user/push_device_mgt/delete <br/> <b>Scopes required:</b> <br>   * internal_push_device_delete ", response = Void.class, authorizations = {
        @Authorization(value = "BasicAuth"),
        @Authorization(value = "OAuth2", scopes = {

        })
    }, tags={ "admin", })
    @ApiResponses(value = {
        @ApiResponse(code = 204, message = "Device was removed", response = Void.class),
        @ApiResponse(code = 401, message = "Unauthorized", response = Void.class),
        @ApiResponse(code = 403, message = "Forbidden", response = Void.class),
        @ApiResponse(code = 500, message = "Server Error", response = ErrorDTO.class)
    })
    public Response userIdPushAuthDevicesDeviceIdDelete(@ApiParam(value = "ID of user",required=true) @PathParam("userId") String userId, @ApiParam(value = "Unique Id of device",required=true) @PathParam("deviceId") String deviceId) {

        return delegate.userIdPushAuthDevicesDeviceIdDelete(userId,  deviceId );
    }

    @Valid
    @GET
    @Path("/{userId}/push-auth/devices/{deviceId}")

    @Produces({ "application/json" })
    @ApiOperation(value = "Get a device by deviceId. ", notes = "This API is used by an admin to retrieve a registered device by the deviceId.<br/> <b>Permission required:</b>  * /permission/admin/manage/identity/user/push_device_mgt/view <br/>   <b>Scopes required:</b> * internal_push_device_view ", response = DeviceDTO.class, authorizations = {
        @Authorization(value = "BasicAuth"),
        @Authorization(value = "OAuth2", scopes = {

        })
    }, tags={ "admin", })
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Requested device of the user", response = DeviceDTO.class),
        @ApiResponse(code = 401, message = "Unauthorized", response = Void.class),
        @ApiResponse(code = 403, message = "Forbidden", response = Void.class),
        @ApiResponse(code = 404, message = "Not Found", response = ErrorDTO.class),
        @ApiResponse(code = 500, message = "Server Error", response = ErrorDTO.class)
    })
    public Response userIdPushAuthDevicesDeviceIdGet(@ApiParam(value = "ID of user",required=true) @PathParam("userId") String userId, @ApiParam(value = "ID of device to return",required=true) @PathParam("deviceId") String deviceId) {

        return delegate.userIdPushAuthDevicesDeviceIdGet(userId,  deviceId );
    }

    @Valid
    @GET
    @Path("/{userId}/push-auth/devices")

    @Produces({ "application/json" })
    @ApiOperation(value = "Get user's registered device list. ", notes = "This API is used by admins to get a list of devices registered under a user.<br/> <b>Permission required:</b>  * /permission/admin/manage/identity/user/push_device_mgt/list <br/>   <b>Scopes required:</b> * internal_push_device_list ", response = Object.class, responseContainer = "List", authorizations = {
        @Authorization(value = "BasicAuth"),
        @Authorization(value = "OAuth2", scopes = {

        })
    }, tags={ "admin" })
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "List of registered devices of the user", response = Object.class, responseContainer = "List"),
        @ApiResponse(code = 401, message = "Unauthorized", response = Void.class),
        @ApiResponse(code = 403, message = "Forbidden", response = Void.class),
        @ApiResponse(code = 404, message = "Not Found", response = ErrorDTO.class),
        @ApiResponse(code = 500, message = "Server Error", response = ErrorDTO.class)
    })
    public Response userIdPushAuthDevicesGet(@ApiParam(value = "ID of user",required=true) @PathParam("userId") String userId) {

        return delegate.userIdPushAuthDevicesGet(userId );
    }

}
