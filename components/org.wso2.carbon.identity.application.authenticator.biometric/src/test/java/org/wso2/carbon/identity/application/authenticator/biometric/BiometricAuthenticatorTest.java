/*
 * Copyright (c) 2019, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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
package org.wso2.carbon.identity.application.authenticator.biometric;

import org.apache.axis2.context.ConfigurationContextFactory;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.modules.testng.PowerMockObjectFactory;
import org.testng.Assert;
import org.testng.IObjectFactory;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.ObjectFactory;
import org.testng.annotations.Test;
import org.wso2.carbon.extension.identity.helper.FederatedAuthenticatorUtil;
import org.wso2.carbon.identity.application.authentication.framework.config.builder.FileBasedConfigurationBuilder;
import org.wso2.carbon.identity.application.authentication.framework.util.FrameworkUtils;
import org.wso2.carbon.identity.core.util.IdentityTenantUtil;
import org.wso2.carbon.identity.mgt.config.ConfigBuilder;
import org.wso2.carbon.identity.mgt.mail.NotificationBuilder;
import org.wso2.carbon.identity.testutil.powermock.PowerMockIdentityBaseTest;
import org.wso2.carbon.utils.multitenancy.MultitenantUtils;
import javax.servlet.http.HttpServletRequest;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.powermock.api.mockito.PowerMockito.mockStatic;

@RunWith(PowerMockRunner.class)
@PrepareForTest({BiometricAuthenticator.class, FileBasedConfigurationBuilder.class, FederatedAuthenticatorUtil.class,
        FrameworkUtils.class, MultitenantUtils.class, IdentityTenantUtil.class, ConfigurationContextFactory.class,
        ConfigBuilder.class, NotificationBuilder.class})
@PowerMockIgnore({"javax.crypto.*" })
public class BiometricAuthenticatorTest extends PowerMockIdentityBaseTest {
    private BiometricAuthenticator biometricAuthenticator;
    @Mock private HttpServletRequest httpServletRequest;

    @BeforeMethod
    public void setUp() {

        biometricAuthenticator = new BiometricAuthenticator();
        initMocks(this);
        mockStatic(FileBasedConfigurationBuilder.class);
        mockStatic(FederatedAuthenticatorUtil.class);
        mockStatic(FrameworkUtils.class);
        mockStatic(MultitenantUtils.class);
        mockStatic(IdentityTenantUtil.class);
        mockStatic(ConfigurationContextFactory.class);
        mockStatic(ConfigBuilder.class);
        mockStatic(NotificationBuilder.class);
    }

    @Test(description = "Test case for canHandle() method true case.")
    public void testCanHandle() {

        when(httpServletRequest.getParameter(BiometricAuthenticatorConstants.SIGNED_CHALLENGE)).thenReturn("true");
        Assert.assertTrue(biometricAuthenticator.canHandle(httpServletRequest));
    }

    @Test(description = "Test case for canHandle() method false case.")
    public void testCanHandleFalse() {

        when(httpServletRequest.getParameter(BiometricAuthenticatorConstants.SIGNED_CHALLENGE)).thenReturn(null);
        Assert.assertFalse(biometricAuthenticator.canHandle(httpServletRequest));
    }

    @Test(description = "Test case for getContextIdentifier() method.")
    public void testGetContextIdentifier() {

        when(httpServletRequest.getParameter("sessionDataKey")).thenReturn("234567890");
        Assert.assertEquals(biometricAuthenticator.getContextIdentifier(httpServletRequest), "234567890");
        when(httpServletRequest.getParameter("sessionDataKey")).thenReturn(null);
        Assert.assertNull(biometricAuthenticator.getContextIdentifier(httpServletRequest));
    }
//
    @Test(description = "Test case for getFriendlyName() method.")
    public void testGetFriendlyName() {

        Assert.assertEquals(biometricAuthenticator.getFriendlyName(),
                BiometricAuthenticatorConstants.AUTHENTICATOR_FRIENDLY_NAME);
    }
//
    @Test(description = "Test case for getAuthenticatorName() method.")
    public void testGetName() {

        Assert.assertEquals(biometricAuthenticator.getName(), BiometricAuthenticatorConstants.AUTHENTICATOR_NAME);
    }
//
//    /**
//     * Gets the authenticator name.
//     */
//    private void getAuthenticatorName() {
//        when(context.getSequenceConfig()).thenReturn(sequenceConfig);
//        when(sequenceConfig.getStepMap()).thenReturn(mockedMap);
//        when(mockedMap.get(anyObject())).thenReturn(stepConfig);
//        when(stepConfig.getAuthenticatorList()).thenReturn(authenticatorList);
//        when(authenticatorList.iterator()).thenReturn(iterator);
//        when(iterator.next()).thenReturn(authenticatorConfig);
//        when(authenticatorConfig.getName()).thenReturn(BiometricAuthenticatorConstants.AUTHENTICATOR_NAME);
//    }
//
//    /**
//     * Get the EmailOTP error page.
//     * @param parameters Parameters map.
//     */
//    private void getBiometricWaitPage(Map<String, String> parameters) {
//        parameters.put(BiometricAuthenticatorConstants.DOMAIN_NAME + BiometricAuthenticatorConstants.WAIT_PAGE,
//                "emailotpauthenticationendpoint/custom/error.jsp");
//        authenticatorConfig.setParameterMap(parameters);
//        PowerMockito.when(FileBasedConfigurationBuilder.getInstance()).thenReturn(fileBasedConfigurationBuilder);
//        PowerMockito.when(fileBasedConfigurationBuilder.getAuthenticatorBean(anyString()))
//                .thenReturn(authenticatorConfig);
//    }

    @ObjectFactory
    public IObjectFactory getObjectFactory() {
        return new PowerMockObjectFactory();
    }
}
