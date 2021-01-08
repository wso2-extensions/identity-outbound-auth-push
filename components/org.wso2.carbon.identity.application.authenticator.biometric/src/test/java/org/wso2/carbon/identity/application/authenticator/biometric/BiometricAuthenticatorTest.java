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
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Spy;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.modules.testng.PowerMockObjectFactory;
import org.powermock.reflect.Whitebox;
import org.testng.Assert;
import org.testng.IObjectFactory;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.ObjectFactory;
import org.testng.annotations.Test;
import org.wso2.carbon.extension.identity.helper.FederatedAuthenticatorUtil;
import org.wso2.carbon.identity.application.authentication.framework.config.builder.FileBasedConfigurationBuilder;
import org.wso2.carbon.identity.application.authentication.framework.context.AuthenticationContext;
import org.wso2.carbon.identity.application.authentication.framework.exception.AuthenticationFailedException;
import org.wso2.carbon.identity.application.authentication.framework.model.AuthenticatedUser;
import org.wso2.carbon.identity.application.authentication.framework.util.FrameworkUtils;
import org.wso2.carbon.identity.application.common.model.Property;
import org.wso2.carbon.identity.core.util.IdentityTenantUtil;
import org.wso2.carbon.identity.mgt.config.ConfigBuilder;
import org.wso2.carbon.identity.mgt.mail.NotificationBuilder;
import org.wso2.carbon.identity.testutil.powermock.PowerMockIdentityBaseTest;
import org.wso2.carbon.utils.multitenancy.MultitenantUtils;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
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

    @Mock
    private HttpServletRequest httpServletRequest;

    @Mock
    private HttpServletResponse httpServletResponse;

    @Spy
    private BiometricAuthenticator spy;

    @Spy
    private AuthenticationContext context;


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

    @Test
    public void testGetConfigurationProperties() {
        List<Property> configProperties = new ArrayList<Property>();
        Property firebaseServerKey = new Property();
        configProperties.add(firebaseServerKey);
        Property fcmUrl = new Property();
        configProperties.add(fcmUrl);
        Assert.assertEquals(configProperties.size(), biometricAuthenticator.getConfigurationProperties().size());
    }

    @ObjectFactory
    public IObjectFactory getObjectFactory() {
        return new PowerMockObjectFactory();
    }

    @Test
    public void testGetDevicesPage() throws Exception {
        BiometricAuthenticator authenticator = PowerMockito.spy(biometricAuthenticator);
        Assert.assertEquals(Whitebox.invokeMethod(authenticator, "getDevicesPage",
                context),
                "authenticationendpoint/biometricdevices.jsp");
    }

    @Test
    public void testGetWaitPage() throws Exception {
        BiometricAuthenticator authenticator = PowerMockito.spy(biometricAuthenticator);
        Assert.assertEquals(Whitebox.invokeMethod(authenticator, "getWaitPage",
                context),
                "authenticationendpoint/waitpage.jsp");
    }

    @Test(expectedExceptions = {AuthenticationFailedException.class})
    public void testInitiateAuthenticationRequestWithoutAuthenticatedUser() throws Exception {
        mockStatic(FederatedAuthenticatorUtil.class);
        mockStatic(FrameworkUtils.class);
        context.setTenantDomain("carbon.super");
        FederatedAuthenticatorUtil.setUsernameFromFirstStep(context);
        Whitebox.invokeMethod(biometricAuthenticator, "initiateAuthenticationRequest",
                httpServletRequest, httpServletResponse, context);
    }

    @Test(expectedExceptions = {AuthenticationFailedException.class})
    public void testProcessAuthenticationRequestWithInvalidSignature() throws Exception {
        when(httpServletRequest.getParameter("deviceId")).thenReturn("636d2e52-e306-417e-bdbf-b828d635a686");
        when(httpServletRequest.getParameter("signedChallenge")).thenReturn("eiafhiuehfiu8499234u93ddljdskhf");
        when(httpServletRequest.getParameter("signature")).thenReturn("fewefuiehsif3249847042hkfhkshdfk3974982374983274dkjh3392");
        PowerMockito.when(biometricAuthenticator, "verifySignature").thenReturn(true);
        context.setTenantDomain("carbon.super");
        Whitebox.invokeMethod(biometricAuthenticator, "processAuthenticationResponse",
                httpServletRequest, httpServletResponse, context);
    }

    @Test
    public void testProcessAuthenticationResponse() throws Exception {
        when(httpServletRequest.getParameter("deviceId")).thenReturn("123456");
        when(httpServletRequest.getParameter("signedChallenge")).thenReturn("123456");
        when(httpServletRequest.getParameter("signature")).thenReturn("123456");
        AuthenticatedUser authenticatedUser = new AuthenticatedUser();
        authenticatedUser.setAuthenticatedSubjectIdentifier("admin");
        when((AuthenticatedUser) context.getProperty("authenticatedUser")).thenReturn(authenticatedUser);
        Whitebox.invokeMethod(biometricAuthenticator, "processAuthenticationResponse",
                httpServletRequest, httpServletResponse, context);
    }

    @Test
    public void testInitiateAuthenticationRequest() throws Exception {
        mockStatic(FederatedAuthenticatorUtil.class);
        mockStatic(FrameworkUtils.class);
        context.setTenantDomain("carbon.super");
        AuthenticatedUser authenticatedUser = new AuthenticatedUser();
        authenticatedUser.setAuthenticatedSubjectIdentifier("admin");
        when((AuthenticatedUser) context.getProperty("authenticatedUser")).thenReturn(authenticatedUser);
        FederatedAuthenticatorUtil.setUsernameFromFirstStep(context);
        Whitebox.invokeMethod(biometricAuthenticator, "initiateAuthenticationRequest",
                httpServletRequest, httpServletResponse, context);
    }

}
