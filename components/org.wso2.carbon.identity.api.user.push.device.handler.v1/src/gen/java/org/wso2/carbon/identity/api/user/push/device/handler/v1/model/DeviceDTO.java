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
import io.swagger.annotations.ApiModelProperty;

import java.util.Objects;
import javax.validation.Valid;

public class DeviceDTO  {
  
    private String deviceId;
    private String name;
    private String model;
    private String pushId;
    private Object registrationTime;
    private Object lastUsedTime;

    /**
    **/
    public DeviceDTO deviceId(String deviceId) {

        this.deviceId = deviceId;
        return this;
    }
    
    @ApiModelProperty(example = "b03f90c9-6723-48f6-863b-a35f1ac77f57", value = "")
    @JsonProperty("deviceId")
    @Valid
    public String getDeviceId() {
        return deviceId;
    }
    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    /**
    **/
    public DeviceDTO name(String name) {

        this.name = name;
        return this;
    }
    
    @ApiModelProperty(example = "My Iphone", value = "")
    @JsonProperty("name")
    @Valid
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    /**
    **/
    public DeviceDTO model(String model) {

        this.model = model;
        return this;
    }
    
    @ApiModelProperty(example = "Iphone 8", value = "")
    @JsonProperty("model")
    @Valid
    public String getModel() {
        return model;
    }
    public void setModel(String model) {
        this.model = model;
    }

    /**
    **/
    public DeviceDTO pushId(String pushId) {

        this.pushId = pushId;
        return this;
    }
    
    @ApiModelProperty(example = "fuRr8s_eQrmB88nu5Tz8oa:APA91bFMqYbuzDYyOGK28VoiLHWYXZYzGNVg3tfxfNwKPH-jDIFpNDdUHkmq5wqBUySYZnuHfpycyQvUrPhwV3UZ1YzjUNLvb9gzFZudfJd1N3fWuU0w2nq_hVJc0UPRabvNPuJy8wMB", value = "")
    @JsonProperty("pushId")
    @Valid
    public String getPushId() {
        return pushId;
    }
    public void setPushId(String pushId) {
        this.pushId = pushId;
    }

    /**
    **/
    public DeviceDTO registrationTime(Object registrationTime) {

        this.registrationTime = registrationTime;
        return this;
    }
    
    @ApiModelProperty(example = "2019-11-26T05:16:19.932Z", value = "")
    @JsonProperty("registrationTime")
    @Valid
    public Object getRegistrationTime() {
        return registrationTime;
    }
    public void setRegistrationTime(Object registrationTime) {
        this.registrationTime = registrationTime;
    }

    /**
    **/
    public DeviceDTO lastUsedTime(Object lastUsedTime) {

        this.lastUsedTime = lastUsedTime;
        return this;
    }
    
    @ApiModelProperty(example = "2019-11-26T05:16:19.932Z", value = "")
    @JsonProperty("lastUsedTime")
    @Valid
    public Object getLastUsedTime() {
        return lastUsedTime;
    }
    public void setLastUsedTime(Object lastUsedTime) {
        this.lastUsedTime = lastUsedTime;
    }



    @Override
    public boolean equals(java.lang.Object o) {

        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        DeviceDTO deviceDTO = (DeviceDTO) o;
        return Objects.equals(this.deviceId, deviceDTO.deviceId) &&
            Objects.equals(this.name, deviceDTO.name) &&
            Objects.equals(this.model, deviceDTO.model) &&
            Objects.equals(this.pushId, deviceDTO.pushId) &&
            Objects.equals(this.registrationTime, deviceDTO.registrationTime) &&
            Objects.equals(this.lastUsedTime, deviceDTO.lastUsedTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(deviceId, name, model, pushId, registrationTime, lastUsedTime);
    }

    @Override
    public String toString() {

        StringBuilder sb = new StringBuilder();
        sb.append("class DeviceDTO {\n");
        
        sb.append("    deviceId: ").append(toIndentedString(deviceId)).append("\n");
        sb.append("    name: ").append(toIndentedString(name)).append("\n");
        sb.append("    model: ").append(toIndentedString(model)).append("\n");
        sb.append("    pushId: ").append(toIndentedString(pushId)).append("\n");
        sb.append("    registrationTime: ").append(toIndentedString(registrationTime)).append("\n");
        sb.append("    lastUsedTime: ").append(toIndentedString(lastUsedTime)).append("\n");
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

