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
 */

package org.wso2.carbon.identity.application.authenticator.push.device.handler;

import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.wso2.carbon.identity.application.authentication.framework.util.FrameworkUtils;
import org.wso2.carbon.identity.application.authenticator.push.device.handler.cache.PushDeviceHandlerCacheKey;
import org.wso2.carbon.identity.application.authenticator.push.device.handler.cache.RegistrationRequestChallengeCache;
import org.wso2.carbon.identity.application.authenticator.push.device.handler.cache.RegistrationRequestChallengeCacheEntry;
import org.wso2.carbon.identity.application.authenticator.push.device.handler.dao.DeviceDAOImpl;
import org.wso2.carbon.identity.application.authenticator.push.device.handler.impl.DeviceHandlerImpl;
import org.wso2.carbon.identity.application.authenticator.push.device.handler.model.Device;
import org.wso2.carbon.identity.application.authenticator.push.device.handler.model.RegistrationDiscoveryData;
import org.wso2.carbon.identity.application.authenticator.push.device.handler.model.RegistrationRequest;
import org.wso2.carbon.identity.core.util.IdentityTenantUtil;
import org.wso2.carbon.user.core.common.User;
import org.wso2.carbon.utils.multitenancy.MultitenantUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;

import static org.mockito.Mockito.spy;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.when;

/**
 * Test suite for the device handler component.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({DeviceDAOImpl.class, PushDeviceHandlerCacheKey.class,
        FrameworkUtils.class, MultitenantUtils.class, IdentityTenantUtil.class,
        RegistrationRequestChallengeCache.class, RegistrationRequestChallengeCacheEntry.class,
        DeviceDAOImpl.class, Device.class, RegistrationDiscoveryData.class,
        RegistrationRequest.class})
@PowerMockIgnore({"javax.crypto.*" })

public class PushDeviceHandlerTest {
    @Mock
    DeviceHandlerImpl deviceHandler;

    @Mock
    RegistrationRequestChallengeCache cache;
    @BeforeMethod
    public void setUp() {
        deviceHandler = spy(new DeviceHandlerImpl());
        initMocks(this);
        mockStatic(DeviceDAOImpl.class);
        mockStatic(PushDeviceHandlerCacheKey.class);
        mockStatic(FrameworkUtils.class);
        mockStatic(IdentityTenantUtil.class);
        mockStatic(MultitenantUtils.class);
        mockStatic(RegistrationRequestChallengeCache.class);
        mockStatic(RegistrationRequestChallengeCacheEntry.class);
        mockStatic(DeviceDAOImpl.class);
        mockStatic(RegistrationRequest.class);
    }

    @Test
    public void mockGetAuthenticatedUser() throws Exception {
        User user = new User();
        user.setUsername("avishka");
        user.setUserStoreDomain("PRIMARY");
        user.setTenantDomain("carbon.super");
        when(deviceHandler, "getAuthenticatedUser").thenReturn(user);
    }

    @Test
    public void mockValidateSignature() throws Exception {
        when(deviceHandler, "verifySignature").thenReturn(true);
    }

    @Test
    public void mockListDevices() throws Exception {
        ArrayList<Device> devices = new ArrayList<>();
        devices.add(new Device("6b366130-ae83-474a-9e6e-23560fb0cac9", "My Test Device 1",
                "SAMSUNG - S20", "YREIUWYREIsssasassU22W3493472983UEWOwYROUEW",
                "MIIDQjCCAjUGByqGSM44BAEwggIoAoIBAQCPeTXZuarpv6vtiHrPSVG28y7FnjuvNxjo6sSWHz" +
                        "79NgbnQ1GpxBgzObgJ58KuHFObp0dbhdARrbi0eYd1SYRpXKwOjxSzNggooi", new Date(), new Date()));
        devices.add(new Device("636d2e52-e306-417e-bdbf-b828d635a686", "My Test Device 2",
                "SAMSUNG - S20",
                "fLEgbW4-aDk:APA91bH9ZHARffAGZrCz0JJJZhmU-o6DUNxqxsvWy5SEYCX1Ac-" +
                "HxiIrOgKEtL1GFo8BAIw2uv9JIrCOuOW_3ik69g_GPb6noYeoAiKUCPsGDYzM_j4blKfCMB1cuq3NHKGBeoxurieT",
                "MIIDRjCCAjkGByqGSM44BAEwggIsAoIBAQDOO3uX5eK+11qyr9AN+HClumrBa5wTdzOwxA3Mvq+HY2/" +
                        "idG6jGVEaEoK7MtHEx7MfmIJYLSn6orgnOACvf65vfOezWJbD5VbTkuWHsWHkrl8OohpxtsHvCY8I3qwKeFk5qq+" +
                        "JRgTNSLb50SQAFcrtHjv/i9ZM6vxuSfTPVV29ahZjDB08mlOZwe+syha4tEwy9js2GZglNT3+4WmkTG9aR7EhN02" +
                        "q3WZWRCwcHoaAFeSRLjM0WcccmZI7P/P0PNpVc/byYaWNW3ml1YKXS/sx2DkVdNAoqvYexOzN4qj97wa6Z" +
                        "6ZPTh+OHk1dwBMV2W1svdekCPbUWNIvmZP2B8ibAiEA/WDpm6VVqAfhxJJKP1EDBLhAqcio2OVY8iKXBMC1ies" +
                        "CggEAdZHv5xRP4B/loo0di0FQqlrqEcdjjNv1V1uYAhNcowTp8CEGNE/u6iQKtB+eFZnzebeqghdJLjxN7r6d4iL" +
                        "wkYnYi/bDkkHTd9/IUwCUyJGJqZaoOVhjRHcm6Hz3VpipwkDCklmUzJ98h/p1RF6tP3i2IQxgXiYaKbmH/Pk3/AY3dUm" +
                        "+nlUlXtosM/3eX4PJ9poa46ywGU+Z9YImK9TCh+kngSShAIX5M/uGpKGWeGrxUdX5jjBETR18u7Rv/WCFljQJ+" +
                        "0lBVPZrnZIVZ7BLPD6t8qHqrMww14BQWh5qRZ2up75Csie9fUCiy9izGbskhhTfwmc0Za3x8NyVySzWSwOCAQU" +
                        "AAoIBACt7QO6qWOjiqLFyfgRn1+v7sVKPP9zun2cb5WJdhMJuaoSHiNDmebJafQSj4mrkv/WpkHsfwyIPvyrC/uo1" +
                        "rm8bLXMK9DO5cEA3+BLIVuUS3thi0d4tBz+8itz40PdWVlUldSqaavN/VQLEZIBb0SDcr9fbEYq6OvVluH3MpPqemZHs" +
                        "GuuNX+N+yJUDs7J7duwZQB/I/MQTCBek0vsrtq0hprqSrcfbvi0nLwzeXqsIoZh5C7z5K2wfrir6Y8hErZtePZ" +
                        "j98QeYatG4G968mL3tNBtP9wvxDUpfIecCpCNf1mAEL158AXO1CePohTQrBm2hRoFa75VfA1338CaJEHE=",
                new Date(), new Date()));

        when(deviceHandler, "lisDevices").thenReturn(devices);
    }
    @Test
    public void mockGetDevice() throws Exception {
        Device device  = new Device("636d2e52-e306-417e-bdbf-b828d635a686", "My Test Device 2",
                "SAMSUNG - S20", "fLEgbW4-aDk:APA91bH9ZHARffAGZrCz0JJJZhmU-o6DUNxqxsvWy5SEYCX1Ac-" +
                "HxiIrOgKEtL1GFo8BAIw2uv9JIrCOuOW_3ik69g_GPb6noYeoAiKUCPsGDYzM_j4blKfCMB1cuq3NHKGBeoxurieT",
                "MIIDRjCCAjkGByqGSM44BAEwggIsAoIBAQDOO3uX5eK+11qyr9AN+HClumrBa5wTdzOwxA3Mvq+HY2/idG6jGVEaEoK" +
                        "7MtHEx7MfmIJYLSn6orgnOACvf65vfOezWJbD5VbTkuWHsWHkrl8OohpxtsHvCY8I3qwKeFk5qq+" +
                        "JRgTNSLb50SQAFcrtHjv/i9ZM6vxuSfTPVV29ahZjDB08mlOZwe+syha4tEwy9js2GZglNT3+4WmkTG9aR7EhN02q3" +
                        "WZWRCwcHoaAFeSRLjM0WcccmZI7P/P0PNpVc/byYaWNW3ml1YKXS/sx2DkVdNAoqvYexOzN4qj97wa6Z" +
                        "6ZPTh+OHk1dwBMV2W1svdekCPbUWNIvmZP2B8ibAiEA/WDpm6VVqAfhxJJKP1EDBLhAqcio2OVY8iKXBMC1iesCggEAd" +
                        "ZHv5xRP4B/loo0di0FQqlrqEcdjjNv1V1uYAhNcowTp8CEGNE/u6iQKtB+eFZnzebeqghdJLjxN7r6d4iL" +
                        "wkYnYi/bDkkHTd9/IUwCUyJGJqZaoOVhjRHcm6Hz3VpipwkDCklmUzJ98h/p1RF6tP3i2IQxgXiYaKbmH/Pk3/AY3" +
                        "dUm+nlUlXtosM/3eX4PJ9poa46ywGU+Z9YImK9TCh+kngSShAIX5M/uGpKGWeGrxUdX5jjBETR18u7Rv/WCFljQJ+" +
                        "0lBVPZrnZIVZ7BLPD6t8qHqrMww14BQWh5qRZ2up75Csie9fUCiy9izGbskhhTfwmc0Za3x8NyVySzW" +
                        "SwOCAQUAAoIBACt7QO6qWOjiqLFyfgRn1+v7sVKPP9zun2cb5WJdhMJuaoSHiNDmebJafQSj4mrkv/WpkHsfwyIPvy" +
                        "rC/uo1rm8bLXMK9DO5cEA3+BLIVuUS3thi0d4tBz+8itz40PdWVlUldSqaavN/VQLEZIBb0SDcr9fbEYq6OvVluH3Mp" +
                        "PqemZHsGuuNX+N+yJUDs7J7duwZQB/I/MQTCBek0vsrtq0hprqSrcfbvi0nLwzeXqsIoZh5C7z5K2wfrir6Y8hErZte" +
                        "PZj98QeYatG4G968mL3tNBtP9wvxDUpfIecCpCNf1mAEL158AXO1CePohTQrBm2hRoFa75VfA1338CaJEHE=",
                new Date(), new Date());
        when(deviceHandler, "getDevice").thenReturn(device);
    }

    @Test
    public void mockRegistrationCacheEntry() throws Exception {
        cache = spy(RegistrationRequestChallengeCache.getInstance());
        RegistrationRequestChallengeCacheEntry entry =
                new RegistrationRequestChallengeCacheEntry(UUID.randomUUID().toString(),
                "avishka", "carbon.super", false);
        when(cache, "getValueFromCacheByRequestId").thenReturn(entry);
    }

    @Test
    public void testRegisterDevice() throws Exception {
        RegistrationRequest request = new RegistrationRequest();
        request.setDeviceId("636d2e52-e306-417e-bdbf-b828d635a686");
        request.setDeviceName("SAMSUNG - S20");

        Device device = new Device();
        device.setDeviceId(request.getDeviceId());
        device.setDeviceName(request.getDeviceName());
        Whitebox.invokeMethod(deviceHandler, "registerDevice", request, device);
    }

}
