# identity-outbound-auth-push

Documentation for building and installing the push-auth components in the IS.

[![Stackoverflow](https://img.shields.io/badge/Ask%20for%20help%20on-Stackoverflow-orange)](https://stackoverflow.com/questions/tagged/wso2is)
[![Join the chat at https://join.slack.com/t/wso2is/shared_invite/enQtNzk0MTI1OTg5NjM1LTllODZiMTYzMmY0YzljYjdhZGExZWVkZDUxOWVjZDJkZGIzNTE1NDllYWFhM2MyOGFjMDlkYzJjODJhOWQ4YjE](https://img.shields.io/badge/Join%20us%20on-Slack-%23e01563.svg)](https://join.slack.com/t/wso2is/shared_invite/enQtNzk0MTI1OTg5NjM1LTllODZiMTYzMmY0YzljYjdhZGExZWVkZDUxOWVjZDJkZGIzNTE1NDllYWFhM2MyOGFjMDlkYzJjODJhOWQ4YjE)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://github.com/wso2-extensions/identity-outbound-auth-push/blob/master/LICENSE)
[![Twitter](https://img.shields.io/twitter/follow/wso2.svg?style=social&label=Follow)](https://twitter.com/intent/follow?screen_name=wso2)

---

## Setup and Installing Push Authenticator

**Step 1:** Cloning the project

Clone the `identity-outbound-auth-push` repository

**Step 2:** Building the project

Build the project by running `mvn clean install` at the root directory

**Step 3:** Deploying server components

 - Go to `identity-outbound-auth-push/components` →
   - `org.wso2.carbon.identity.application.authenticator.push` → `target`
 - Copy the `.jar` file
 - Go to `<IS_HOME>/repository/components/dropins`
 - Paste the `.jar` file into the dropins directory
 - Alternatively it's possible to drag and drop the `.jar` file to the dropins directory
 - Similarly, repeat the above steps for the components;
   - `org.wso2.carbon.identity.application.authenticator.push.device.handler`
   - `org.wso2.carbon.identity.application.authenticator.push.servlet` 
   - `org.wso2.carbon.identity.application.authenticator.push.common`

**Step 4:** Deploying API components

 - Go to `identity-outbound-auth-push/components` →
   - `org.wso2.carbon.identity.api.user.push.device.common` → `target`
 - Copy the JAR file
 - Go to `<IS_HOME>/repository/deployment/server/webapps` → `api/WEB-INF/lib`
 - Similar to Step 3, paste the `.jar` file in the `lib` directory
 - Do the same for component `org.wso2.carbon.identity.api.user.push.device.handler.v1`

**Step 5:** Updating beans for API

 - Go to `<IS_HOME>/repository/deployment/server/webapps/api/WEB-INF`
 - Open `beans.xml`
 - Add the following

```xml
<beans ...>
    ...

    <import resource="classpath:META-INF/cxf/user-push-device-handler-v1-cxf.xml"/>

    <jaxrs:server id="users" address="/users/v1">
        <jaxrs:serviceBeans>
           ...
           
           <bean class="org.wso2.carbon.identity.api.user.push.device.handler.v1.MeApi"/>
        </jaxrs:serviceBeans>
    </jaxrs:server>
</beans>
```

**Step 5:** Deploying Push Authentication Pages
 - Go to `identity-outbound-auth-push/components` →
   - `org.wso2.carbon.identity.application.authenticator.push` → `src` → `main` → `resources` → `artifacts`
 - Copy `wait.jsp` and `push-device-selection.jsp`
 - Go to `<IS_HOME>/repository/deployment/server/webapps` → `authenticationendpoint`
 - Paste or drop the `JSP` files in the `authenticationendpoint` directory


**Step 6:** Deploying Stylesheets for Pages
 - Go to `identity-outbound-auth-push/components` →
   - `org.wso2.carbon.identity.application.authenticator.push` → `src` → `main` → `resources` → `artifacts` → `css`
 - Copy the `CSS` files
 - Go to `<IS_HOME>/repository/deployment/server/webapps` → `authenticationendpoint/css`
 - Paste or drop the `.css` files in `authenticationendpoint/css` directory

Step 07:
 - Go to `<IS_HOME>/repository/resources/conf/templates/repository/conf/identity`
 - Open `identity.xml.j2`  
 - Scroll down to the “ResourceAccessControl” section
 
The following lines should be added for setting access control for push-auth endpoints
```xml
<ResourceAccessControl>
    <Resource context="(.*)/api/users/v1/me/push-auth/devices" secured="false" http-method="POST" />
    <Resource context="(.*)/api/users/v1/me/push-auth/devices/(.*)/remove" secured="false" http-method="POST" />
    <Resource context="(.*)/api/users/v1/me/push-auth/(.*)" secured="true" http-method="GET, HEAD, POST, PUT, DELETE, PATCH" />
    <Resource context="(.*)/push-auth/check-status" secured="true" http-method="GET" />
</ResourceAccessControl>
```

Add the following to allow multi-tenant support for endpoints

```xml
<TenantContextsToRewrite>
       <Servlet>
           {% for servlet in tenant_context.rewrite.servlets %}
           <Context>{{servlet}}</Context>
           {% endfor %}
           <Context>/push-auth/(.*)</Context>
       </Servlet>
</TenantContextsToRewrite>
```


**Step 08:** Create table for push authentication
```sql
CREATE TABLE IF NOT EXISTS PUSH_AUTHENTICATION_DEVICE (
    ID VARCHAR(255) NOT NULL,
    USER_ID VARCHAR(255) NOT NULL,
    NAME VARCHAR(45) NOT NULL,
    MODEL VARCHAR(45) NOT NULL,
    PUSH_ID VARCHAR(255) NOT NULL,
    PUBLIC_KEY VARCHAR(2048) NOT NULL,
    REGISTRATION_TIME TIMESTAMP,
    LAST_USED_TIME TIMESTAMP,
    PRIMARY KEY (USER_ID, PUSH_ID));
```

**NOTE: In order to communicate with WSO2 IS using a physical device (which will be required for developing an app using the push authentication SDK), the hostname of the IS should be changed to the IP address of the machine running the server.**

**Additionally, the keystore of the IS should be updated for the Android device to allow communication between the physical device and WSO2 IS running locally. This can be done by following this article.**
