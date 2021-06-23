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
import org.wso2.carbon.identity.api.user.push.device.handler.v1.model.DiscoveryDataDTO;
import org.wso2.carbon.identity.api.user.push.device.handler.v1.model.ErrorDTO;
import java.util.List;
import org.wso2.carbon.identity.api.user.push.device.handler.v1.model.PatchDTO;
import org.wso2.carbon.identity.api.user.push.device.handler.v1.model.RegistrationRequestDTO;
import org.wso2.carbon.identity.api.user.push.device.handler.v1.model.RemoveRequestDTO;
import org.wso2.carbon.identity.api.user.push.device.handler.v1.MeApiService;

import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import io.swagger.annotations.*;

import javax.validation.constraints.*;

@Path("/me")
@Api(description = "The me API")

public class MeApi  {

    @Autowired
    private MeApiService delegate;

    @Valid
    @DELETE
    @Path("/push-auth/devices/{deviceId}")

    @Produces({ "application/json" })
    @ApiOperation(value = "Remove a registered device. ", notes = "This API is used to remove a registered device. ", response = Void.class, authorizations = {
        @Authorization(value = "BasicAuth"),
        @Authorization(value = "OAuth2", scopes = {

        })
    }, tags={ "me", })
    @ApiResponses(value = {
        @ApiResponse(code = 204, message = "The device was removed", response = Void.class),
        @ApiResponse(code = 401, message = "Unauthorized", response = Void.class),
        @ApiResponse(code = 403, message = "Forbidden", response = Void.class),
        @ApiResponse(code = 500, message = "Server Error", response = ErrorDTO.class)
    })
    public Response mePushAuthDevicesDeviceIdDelete(@ApiParam(value = "Unique Id of the device",required=true) @PathParam("deviceId") String deviceId) {

        return delegate.mePushAuthDevicesDeviceIdDelete(deviceId );
    }

    @Valid
    @GET
    @Path("/push-auth/devices/{deviceId}")

    @Produces({ "application/json" })
    @ApiOperation(value = "Get a registered device. ", notes = "This API is used to get a specific registered device. ", response = DeviceDTO.class, authorizations = {
        @Authorization(value = "BasicAuth"),
        @Authorization(value = "OAuth2", scopes = {

        })
    }, tags={ "me", })
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Requested registered device of the authenticated user", response = DeviceDTO.class),
        @ApiResponse(code = 401, message = "Unauthorized", response = Void.class),
        @ApiResponse(code = 403, message = "Forbidden", response = Void.class),
        @ApiResponse(code = 404, message = "Not Found", response = ErrorDTO.class),
        @ApiResponse(code = 500, message = "Server Error", response = ErrorDTO.class)
    })
    public Response mePushAuthDevicesDeviceIdGet(@ApiParam(value = "ID of device to return",required=true) @PathParam("deviceId") String deviceId) {

        return delegate.mePushAuthDevicesDeviceIdGet(deviceId );
    }

    @Valid
    @PATCH
    @Path("/push-auth/devices/{deviceId}")
    @Consumes({ "application/json" })
    @Produces({ "application/json" })
    @ApiOperation(value = "Update registered device. ", notes = "This API is used to update attributes of a registered device. ", response = Void.class, authorizations = {
        @Authorization(value = "BasicAuth"),
        @Authorization(value = "OAuth2", scopes = {

        })
    }, tags={ "me", })
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Device was successfully updated", response = Void.class),
        @ApiResponse(code = 400, message = "Bad Request", response = ErrorDTO.class),
        @ApiResponse(code = 401, message = "Unauthorized", response = Void.class),
        @ApiResponse(code = 403, message = "Forbidden", response = Void.class),
        @ApiResponse(code = 404, message = "Not found", response = ErrorDTO.class),
        @ApiResponse(code = 500, message = "Server Error", response = ErrorDTO.class)
    })
    public Response mePushAuthDevicesDeviceIdPatch(@ApiParam(value = "deviceId",required=true) @PathParam("deviceId") String deviceId, @ApiParam(value = "Request to update attributes of the device" ,required=true) @Valid List<PatchDTO> patchDTO) {

        return delegate.mePushAuthDevicesDeviceIdPatch(deviceId,  patchDTO );
    }

    @Valid
    @POST
    @Path("/push-auth/devices/{deviceId}/remove")
    @Consumes({ "application/json" })
    @Produces({ "application/json" })
    @ApiOperation(value = "Remove registered device from within the device. ", notes = "This API is used to remove a registered device from within the device. ", response = Void.class, authorizations = {
        @Authorization(value = "BasicAuth"),
        @Authorization(value = "OAuth2", scopes = {

        })
    }, tags={ "me", })
    @ApiResponses(value = {
        @ApiResponse(code = 204, message = "Device was removed", response = Void.class),
        @ApiResponse(code = 400, message = "Bad Request", response = ErrorDTO.class),
        @ApiResponse(code = 401, message = "Unauthorized", response = Void.class),
        @ApiResponse(code = 403, message = "Forbidden", response = Void.class),
        @ApiResponse(code = 500, message = "Server Error", response = ErrorDTO.class)
    })
    public Response mePushAuthDevicesDeviceIdRemovePost(@ApiParam(value = "Unique Id of the device",required=true) @PathParam("deviceId") String deviceId, @ApiParam(value = "Remove request sent from the device." ,required=true) @Valid RemoveRequestDTO removeRequestDTO) {

        return delegate.mePushAuthDevicesDeviceIdRemovePost(deviceId,  removeRequestDTO );
    }

    @Valid
    @GET
    @Path("/push-auth/devices")

    @Produces({ "application/json" })
    @ApiOperation(value = "Get list of registered devices. ", notes = "This API is used to get a list of registered devices of the authenticated user. ", response = Object.class, responseContainer = "List", authorizations = {
        @Authorization(value = "BasicAuth"),
        @Authorization(value = "OAuth2", scopes = {

        })
    }, tags={ "me", })
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "List of registered devices of the user.", response = Object.class, responseContainer = "List"),
        @ApiResponse(code = 401, message = "Unauthorized", response = Void.class),
        @ApiResponse(code = 403, message = "Forbidden", response = Void.class),
        @ApiResponse(code = 500, message = "Server Error", response = ErrorDTO.class)
    })
    public Response mePushAuthDevicesGet() {

        return delegate.mePushAuthDevicesGet();
    }

    @Valid
    @POST
    @Path("/push-auth/devices")
    @Consumes({ "application/json" })
    @Produces({ "application/json" })
    @ApiOperation(value = "Register a device. ", notes = "This API is used to register a device.<br/> ", response = Void.class, authorizations = {
        @Authorization(value = "BasicAuth"),
        @Authorization(value = "OAuth2", scopes = {

        })
    }, tags={ "me", })
    @ApiResponses(value = {
        @ApiResponse(code = 201, message = "Registered a new device", response = Void.class),
        @ApiResponse(code = 400, message = "Bad Request", response = ErrorDTO.class),
        @ApiResponse(code = 401, message = "Unauthorized", response = Void.class),
        @ApiResponse(code = 403, message = "Forbidden", response = Void.class),
        @ApiResponse(code = 409, message = "Conflict", response = ErrorDTO.class),
        @ApiResponse(code = 500, message = "Server Error", response = ErrorDTO.class)
    })
    public Response mePushAuthDevicesPost(@ApiParam(value = "Request sent by a device for registration." ,required=true) @Valid RegistrationRequestDTO registrationRequestDTO) {

        return delegate.mePushAuthDevicesPost(registrationRequestDTO );
    }

    @Valid
    @GET
    @Path("/push-auth/discovery-data")

    @Produces({ "application/json" })
    @ApiOperation(value = "Generate data for device registration. ", notes = "This API is used to generate discovery data for the device registration QR code. ", response = DiscoveryDataDTO.class, authorizations = {
        @Authorization(value = "BasicAuth"),
        @Authorization(value = "OAuth2", scopes = {

        })
    }, tags={ "me" })
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Successfully generated registration discovery data", response = DiscoveryDataDTO.class),
        @ApiResponse(code = 401, message = "Unauthorized", response = Void.class),
        @ApiResponse(code = 403, message = "Forbidden", response = Void.class),
        @ApiResponse(code = 500, message = "Internal Server Error", response = ErrorDTO.class)
    })
    public Response mePushAuthDiscoveryDataGet() {

        return delegate.mePushAuthDiscoveryDataGet();
    }

}
