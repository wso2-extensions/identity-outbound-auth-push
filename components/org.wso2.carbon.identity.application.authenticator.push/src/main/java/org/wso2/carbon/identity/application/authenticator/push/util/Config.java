/*
 * Copyright (c) 2021, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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

package org.wso2.carbon.identity.application.authenticator.push.util;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.identity.application.authentication.framework.context.AuthenticationContext;
import org.wso2.carbon.identity.application.authenticator.push.PushAuthenticatorConstants;

/**
 * Util class for getting redirect pages from configuration files.
 */
public class Config {

    private static final Log log = LogFactory.getLog(Config.class);

    /**
     * Get the registered devices page from configuration file or use the devices page from constants.
     *
     * @param context the AuthenticationContext
     * @return the waitPage
     */
    public String getDevicesPage(AuthenticationContext context) {

        String devicesPage = getConfiguredDevicesPage(context);
        if (StringUtils.isEmpty(devicesPage)) {
            devicesPage = PushAuthenticatorConstants.DEVICES_PAGE;
            if (log.isDebugEnabled()) {
                log.debug("Default authentication endpoint context is used for devices page.");
            }
        }
        return devicesPage;
    }

    /**
     * Get the devices page url from the configuration file.
     *
     * @param context the AuthenticationContext
     * @return waitPage
     */
    private static String getConfiguredDevicesPage(AuthenticationContext context) {

        return getConfiguration(context, PushAuthenticatorConstants.PUSH_AUTHENTICATION_ENDPOINT_DEVICES_URL);
    }

    /**
     * Get the wait page from the configuration file or use the wait page from constants.
     *
     * @param context the AuthenticationContext
     * @return the waitPage
     */
    public String getWaitPage(AuthenticationContext context) {

        String waitPage = getConfiguredWaitPage(context);
        if (StringUtils.isEmpty(waitPage)) {
            waitPage = PushAuthenticatorConstants.WAIT_PAGE;
            if (log.isDebugEnabled()) {
                log.debug("Default authentication endpoint context is used for wait page.");
            }
        }
        return waitPage;
    }

    /**
     * Get the wait page url from the configuration file.
     *
     * @param context the AuthenticationContext
     * @return waitPage
     */
    private static String getConfiguredWaitPage(AuthenticationContext context) {

        return getConfiguration(context, PushAuthenticatorConstants.PUSH_AUTHENTICATION_ENDPOINT_WAIT_URL);
    }

    /**
     * Read configurations for given authenticator.
     *
     * @param context    Authentication Context.
     * @param configName Name of the config.
     * @return Config value.
     */
    private static String getConfiguration(AuthenticationContext context, String configName) {

        String configValue = null;
        if ((context.getProperty(configName)) != null) {
            configValue = String.valueOf(context.getProperty(configName));
        }
        if (log.isDebugEnabled()) {
            String debugMessage = String.format("Config value for key %s: %s", configName, configValue);
            log.debug(debugMessage);
        }
        return configValue;
    }
}
