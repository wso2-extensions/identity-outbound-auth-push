# WSO2 Push Authenticator React Native SDK

Repository containing the source of WSO2 IS Push Authentication SDK.

[![Stackoverflow](https://img.shields.io/badge/Ask%20for%20help%20on-Stackoverflow-orange)](https://stackoverflow.com/questions/tagged/wso2is)
[![Join the chat at https://join.slack.com/t/wso2is/shared_invite/enQtNzk0MTI1OTg5NjM1LTllODZiMTYzMmY0YzljYjdhZGExZWVkZDUxOWVjZDJkZGIzNTE1NDllYWFhM2MyOGFjMDlkYzJjODJhOWQ4YjE](https://img.shields.io/badge/Join%20us%20on-Slack-%23e01563.svg)](https://join.slack.com/t/wso2is/shared_invite/enQtNzk0MTI1OTg5NjM1LTllODZiMTYzMmY0YzljYjdhZGExZWVkZDUxOWVjZDJkZGIzNTE1NDllYWFhM2MyOGFjMDlkYzJjODJhOWQ4YjE)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://github.com/wso2-extensions/identity-outbound-auth-push/blob/master/LICENSE)
[![Twitter](https://img.shields.io/twitter/follow/wso2.svg?style=social&label=Follow)](https://twitter.com/intent/follow?screen_name=wso2)

---

## Table of Contents

- [Introduction](#introduction)
- [Install](#install)
- [Build Package and Install](#build-package-and-install)
- [Getting Started](#getting-started)
- [APIs](#apis)
- [Usage](#usage)
- [Models](#models)
- [Contribute](#contribute)
    - [Reporting Issues](#reporting-issues)
- [License](#license)

## Introduction

WSO2 auth-push-react-native is a package that can be used for developing a push-based authentication app with React
Native, that can be used for authenticating with WSO2 Identity Server.

## Install


For npm
```
$ npm install @wso2/auth-push-react-native
```
For yarn
```
$ yarn add @wso2/auth-push-react-native
```

For installing on iOS
```
$ cd ios && pod install
```

## Build Package and Install

If you wish to build the SDK on your own and install the packages, the following steps should be followed.

**Step 1:** Clone the project

**Step 2:** Go to the `sdk/package` directory from the terminal

**Step 3:** Run the command `npm run build`

**Step 4:** Create a new directory in the app project for saving the package (eg: `packages`)

**Step 5:** Copy the package directory from the sdk and paste in the new `packages` directory in the app project.
The `src` directory may be removed as it is not required for using the SDK once built.

**Step 6:** Open a terminal at the app project root

**Step 7:** Run `npm install <path-to-package>`

| example: `npm install packages/package` |
|----------------------------------|

**Step 8:** Run `cd ios && pod install` for iOS

****Note: Step 7 will auto-link the package. Any changes made in the `lib` directory will take effect within the project
installing the package.**


## Getting Started

### Initializing the SDK

Add the following in the class scope of `app.js` / `app.ts` to initialize the SDK.

```js
import { DeviceHandlerUtil } from "@wso2/auth-push-react-native";
... 

new DeviceHandlerUtils();
```

## APIs

The following methods can be used for implementing push-based authentication on a mobile app.

| Method                      | Return Type               | Description           |
|-----------------------------|---------------------------|-----------------------|
| [addAccount(qrData, pushId)](#addaccountqrdata-discoverydata-pushid-string)  | `Promise<AccountsInterface>`         | Register the device with an account |
| [removeAccount(account)](#removeaccountaccount-accounts)   | `Promise<string>`           | Unregister the device from the account by passing the saved account object |
| [processAuthRequest(request)](#processauthrequestrequest-json) | `AuthRequestInterface`      | Convert the authentication request data received from the push notification to AuthRequestInterface |
| [sendAuthRequest(<br/>&nbsp;&nbsp;&nbsp;&nbsp;authRequest, <br/>&nbsp;&nbsp;&nbsp;&nbsp;response,<br/>&nbsp;&nbsp;&nbsp;&nbsp;account<br/>)](#sendauthrequestauthrequest-authrequest-response-string-account-account) | `Promise<object>` | Send the authentication response to the server once the user has authorized or denied the request |

## Usage

### addAccount(qrData: [DiscoveryDataInterface](#discoverydatainterface), pushId: string)

Register the device in WSO2 IS with the relevant user account. Once the registration is completed, the SDK returns the
user account object which can be saved in the app by the developer.

```ts
import { AccountService } from "@wso2/auth-push-react-native";
...

let account = new AccountsService();

account.addAccount(qrData, pushId)
    .then((response) => {
        let res = JSON.parse(response);
        if (res.res === "OK") {
            // Do add account success action
        } else if (res.res === "FAILED") {
            // Do add account failed action
        }
    })
    .catch((err) => {
        // Handle error
    });
```

### removeAccount(account: [AccountsInterface](#accountsinterface))
Unregister the device from the WSO2 IS account from within the device.

```ts
import { AccountService } from "from @wso2/auth-push-react-native";
...

let accountsService = new AccountsService();
accountsService.removeAccount(account);
```

### processAuthRequest(request: JSON)
Process the data received from the push notification as an [AuthRequestInterface](#authrequestinterface) object.

```ts
import { AuthorizationService } from "@wso2/auth-push-react-native";
...

let authData = AuthorizationService.processAuthRequest(data);
```

### sendAuthRequest(authRequest: [AuthRequestInterface](#authrequestinterface), response: string, account: [Account](#accounts))
Send the authentication response to WSO2 IS once the user authorizes or denies the request.

```ts
import { AuthorizationService } from "@wso2/auth-push-react-native";
...

AuthorizationService.sendAuthRequest(authData, response, account)
    .then((res) => {
        let response = JSON.parse(res);
        if (response.res === "OK") {
            // Do positive action
        } else if (response.res === "FAILED") {
            // Do negative action
        }
    })
    .catch((err) => {
        // Handle error
    });
```

## Models

### AccountsInterface
```
deviceID: string
username: string
firstName: string
lastName: string
tenantDomain: string
host: string
basePath: string
authenticationEndpoint: string
removeDeviceEndpoint: string
privateKey: string
```

### AuthRequestInterface
```
deviceId: string
challenge: string
sessionDataKey: string
connectionCode: string
displayName: string
username: string
organization: string
applicationName: string
applicationUrl: string
deviceName: string
browserName: string
ipAddress: string
location: string
expiryTime: string
authenticationStatus: string
requestTime: DateTimeInterface
```

### DiscoveryDataInterface
```
deviceId: string
username: string
firstName: string
lastName: string
tenantDomain: string
host: string
basePath: string
registrationEndpoint: string
authenticationEndpoint: string
removeDeviceEndpoint: string
challenge: string
```

## Development
- For testing out the use of the SDK in a React Native application, the following steps should be followed. As the IS running locally will not have a valid CA certificate, any request sent from an Android device will get blocked.
- Changing the keystore of the IS with the required credentials enables the possibility of getting the requests to work.
- As a physical device will be required for scanning QR code, the DNS and the IP address of the host machine should be included in the certificate.
- Follow [this article](https://kushanbhareti.medium.com/react-native-android-fixing-ssl-issues-for-communicating-with-local-identity-server-f126b0ce69a9) to generate a new keystore according to the above requirements.


## Contribute

Please read [Contributing to the Code Base](http://wso2.github.io/) for details on our code of conduct, and the process for submitting pull requests to us.

### Reporting Issues

We encourage you to report issues, improvements, and feature requests creating [Github Issues](https://github.com/asgardeo/asgardeo-react-native-oidc-sdk/issues).

Important: And please be advised that security issues must be reported to security@wso2com, not as GitHub issues, in order to reach the proper audience. We strongly advise following the WSO2 Security Vulnerability Reporting Guidelines when reporting the security issues.

## License

This project is licensed under the Apache License 2.0. See the [LICENSE](../LICENSE) file for details.






