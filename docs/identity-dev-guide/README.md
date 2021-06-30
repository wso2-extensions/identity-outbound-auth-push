# Push-Based Authenticator Admin Documentation

Documentation for an Identity Developer to set up Push-based authentication in an MFA flow.

[![Stackoverflow](https://img.shields.io/badge/Ask%20for%20help%20on-Stackoverflow-orange)](https://stackoverflow.com/questions/tagged/wso2is)
[![Join the chat at https://join.slack.com/t/wso2is/shared_invite/enQtNzk0MTI1OTg5NjM1LTllODZiMTYzMmY0YzljYjdhZGExZWVkZDUxOWVjZDJkZGIzNTE1NDllYWFhM2MyOGFjMDlkYzJjODJhOWQ4YjE](https://img.shields.io/badge/Join%20us%20on-Slack-%23e01563.svg)](https://join.slack.com/t/wso2is/shared_invite/enQtNzk0MTI1OTg5NjM1LTllODZiMTYzMmY0YzljYjdhZGExZWVkZDUxOWVjZDJkZGIzNTE1NDllYWFhM2MyOGFjMDlkYzJjODJhOWQ4YjE)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://github.com/wso2-extensions/identity-outbound-auth-push/blob/master/LICENSE)
[![Twitter](https://img.shields.io/twitter/follow/wso2.svg?style=social&label=Follow)](https://twitter.com/intent/follow?screen_name=wso2)

---

##Setting up the Identity Provider

**Step 1:**
Run the WSO2 Identity Server and login to the management console using credentials for an admin account `(https://<hostname>:<port-number>/carbon)`.

Step 2:
Navigate to `Identity Providers` → `Add`

Step 3:
Register a new IDP for push based authentication

<img width="569" alt="Register new IDP" src="https://user-images.githubusercontent.com/19253380/124005013-3936b180-d9f6-11eb-803f-e07e5c2b86c9.png">

**Step 4:**
Navigate to `Federated Authenticators` → `Push Authentication Configuration` and enable the Push Authenticator 

<img width="626" alt="Enable push auth" src="https://user-images.githubusercontent.com/19253380/124005399-a6e2dd80-d9f6-11eb-9316-897412bed30b.png">

**Step 5:**
A Firebase application should be created in the Firebase console to support the Push-based authentication mobile application that's developed.

 - The Firebase Server Key required for the above configuration can be found in the Firebase console at `<Firebase-Application-Name>` → `Project Settings` → `Cloud Messaging`
 - The Firebase URL should be: https://fcm.googleapis.com/fcm/send

## Using Push Authentication in MFA

**Step 1:**
Run the WSO2 Identity Server and login to the management console using credentials for an admin account.  (https://<hostname>:<port-number>/carbon)

**Step 2:**
Navigate to `Service Providers` and either add a new Service Provider or Edit an existing one

**Note: A service provider (pickup-dispatch) can be configured by following this documentation.**

**Step 3:**
Navigate to the `Local and Outbound Authentication Configuration` section of the registered Service Provider.

<img width="621" alt="Outbound auth config" src="https://user-images.githubusercontent.com/19253380/124005909-42744e00-d9f7-11eb-92ae-48b8c300410a.png">

**Step 4:**
Go to `Advanced Configuration` and set up a multi-factor authentication flow as below.

<img width="616" alt="Screenshot 2021-06-30 at 23 07 51" src="https://user-images.githubusercontent.com/19253380/124006521-01306e00-d9f8-11eb-8457-7190e68da69e.png">

Once the configuration is done, press update to save the changes. 


