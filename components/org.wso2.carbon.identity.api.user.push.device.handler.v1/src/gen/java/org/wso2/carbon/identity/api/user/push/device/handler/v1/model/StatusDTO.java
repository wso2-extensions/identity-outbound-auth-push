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

package org.wso2.carbon.identity.api.user.push.device.handler.v1.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import javax.validation.constraints.*;

/**
 * Model for the status for the completion of a request
 **/

import io.swagger.annotations.*;
import java.util.Objects;
import javax.validation.Valid;
import javax.xml.bind.annotation.*;
@ApiModel(description = "Model for the status for the completion of a request")
public class StatusDTO  {
  
    private String deviceId;
    private String operation;
    private String status;

    /**
    * Unique ID of the device
    **/
    public StatusDTO deviceId(String deviceId) {

        this.deviceId = deviceId;
        return this;
    }
    
    @ApiModelProperty(example = "b03f90c9-6723-48f6-863b-a35f1ac77f57", required = true, value = "Unique ID of the device")
    @JsonProperty("deviceId")
    @Valid
    @NotNull(message = "Property deviceId cannot be null.")

    public String getDeviceId() {
        return deviceId;
    }
    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    /**
    * Operation handled by the request
    **/
    public StatusDTO operation(String operation) {

        this.operation = operation;
        return this;
    }
    
    @ApiModelProperty(example = "REMOVE", value = "Operation handled by the request")
    @JsonProperty("operation")
    @Valid
    public String getOperation() {
        return operation;
    }
    public void setOperation(String operation) {
        this.operation = operation;
    }

    /**
    * Result status of the request
    **/
    public StatusDTO status(String status) {

        this.status = status;
        return this;
    }
    
    @ApiModelProperty(example = "SUCCESSFUL", required = true, value = "Result status of the request")
    @JsonProperty("status")
    @Valid
    @NotNull(message = "Property status cannot be null.")

    public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status = status;
    }



    @Override
    public boolean equals(java.lang.Object o) {

        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        StatusDTO statusDTO = (StatusDTO) o;
        return Objects.equals(this.deviceId, statusDTO.deviceId) &&
            Objects.equals(this.operation, statusDTO.operation) &&
            Objects.equals(this.status, statusDTO.status);
    }

    @Override
    public int hashCode() {
        return Objects.hash(deviceId, operation, status);
    }

    @Override
    public String toString() {

        StringBuilder sb = new StringBuilder();
        sb.append("class StatusDTO {\n");
        
        sb.append("    deviceId: ").append(toIndentedString(deviceId)).append("\n");
        sb.append("    operation: ").append(toIndentedString(operation)).append("\n");
        sb.append("    status: ").append(toIndentedString(status)).append("\n");
        sb.append("}");
        return sb.toString();
    }

    /**
    * Convert the given object to string with each line indented by 4 spaces
    * (except the first line).
    */
    private String toIndentedString(java.lang.Object o) {

        if (o == null) {
            return "null";
        }
        return o.toString().replace("\n", "\n");
    }
}

