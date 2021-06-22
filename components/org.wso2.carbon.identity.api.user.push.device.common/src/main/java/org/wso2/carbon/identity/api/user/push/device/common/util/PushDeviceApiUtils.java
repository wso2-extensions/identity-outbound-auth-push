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

package org.wso2.carbon.identity.api.user.push.device.common.util;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.identity.api.user.common.error.APIError;
import org.wso2.carbon.identity.api.user.common.error.ErrorResponse;
import org.wso2.carbon.identity.application.authenticator.push.common.exception.PushAuthTokenValidationException;
import org.wso2.carbon.identity.application.authenticator.push.device.handler.exception.PushDeviceHandlerClientException;
import org.wso2.carbon.identity.application.authenticator.push.device.handler.exception.PushDeviceHandlerServerException;
import org.wso2.carbon.user.api.UserStoreException;

import javax.ws.rs.core.Response;

/**
 * The class which handles API errors.
 */
public class PushDeviceApiUtils {

    private static final Log log = LogFactory.getLog(PushDeviceApiUtils.class);

    public static APIError handleException(Exception e, PushDeviceApiConstants.ErrorMessages errorEnum,
                                           String... data) {

        ErrorResponse errorResponse;
        if (data != null) {
            errorResponse = getErrorBuilder(errorEnum).build(log, e, String.format(errorEnum.getDescription(),
                    (Object[]) data));
        } else {
            errorResponse = getErrorBuilder(errorEnum).build(log, e, errorEnum.getDescription());
        }
        if (errorEnum.getMessage() == null) {
            errorEnum.setMessage(e.getMessage());
        }
        if (e instanceof PushAuthTokenValidationException) {

            return new APIError(Response.Status.UNAUTHORIZED, errorResponse);
        } else if (e instanceof PushDeviceHandlerClientException) {
            if (errorEnum.getCode().equals(PushDeviceApiConstants
                    .ErrorMessages.ERROR_CODE_GET_DEVICE_CLIENT_ERROR.getCode())) {
                return new APIError(Response.Status.NOT_FOUND, errorResponse);
            } else {
                return new APIError(Response.Status.BAD_REQUEST, errorResponse);
            }
        } else if (e instanceof PushDeviceHandlerServerException) {
            return new APIError(Response.Status.INTERNAL_SERVER_ERROR, errorResponse);
        } else if (e instanceof UserStoreException) {
            return new APIError(Response.Status.INTERNAL_SERVER_ERROR, errorResponse);
        } else {
            return new APIError(Response.Status.INTERNAL_SERVER_ERROR, errorResponse);
        }
    }

    private static ErrorResponse.Builder getErrorBuilder(PushDeviceApiConstants.ErrorMessages errorEnum) {

        return new ErrorResponse.Builder().withCode(errorEnum.getCode()).withMessage(errorEnum.getMessage())
                .withDescription(errorEnum.getDescription());
    }
}
