# identity-outbound-auth-push

Documentation explaining the architecture of the Push-based authenticatr.

[![Stackoverflow](https://img.shields.io/badge/Ask%20for%20help%20on-Stackoverflow-orange)](https://stackoverflow.com/questions/tagged/wso2is)
[![Join the chat at https://join.slack.com/t/wso2is/shared_invite/enQtNzk0MTI1OTg5NjM1LTllODZiMTYzMmY0YzljYjdhZGExZWVkZDUxOWVjZDJkZGIzNTE1NDllYWFhM2MyOGFjMDlkYzJjODJhOWQ4YjE](https://img.shields.io/badge/Join%20us%20on-Slack-%23e01563.svg)](https://join.slack.com/t/wso2is/shared_invite/enQtNzk0MTI1OTg5NjM1LTllODZiMTYzMmY0YzljYjdhZGExZWVkZDUxOWVjZDJkZGIzNTE1NDllYWFhM2MyOGFjMDlkYzJjODJhOWQ4YjE)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://github.com/wso2-extensions/identity-outbound-auth-push/blob/master/LICENSE)
[![Twitter](https://img.shields.io/twitter/follow/wso2.svg?style=social&label=Follow)](https://twitter.com/intent/follow?screen_name=wso2)

---


## Contents
- [Introduction](#introduction)
- [Components](#components)
    - [Authenticator Component](#authenticator-component)
    - [Device Handler Component](#device-handler-component)
    - [Servlet Component](#servlet-component)
    - [Common Component](#common-component)
    - [API Components](#api-components)
- [Architecture](#architecture)    
- [Design Decisions](#design-decisions)
- [Security Decisions](#security-decisions)
- [Sample Requests](#sample-requests)


## Introduction

With the growth of the internet reaching its prime the requirement of Identity and Access Management system has become
an essential for many organisations. IAM systems have provided various benefits to organisations and multiple methods
for authenticating users into internal and external services. With the increase of the user count and the increasing
number of threats, it has become a practice to opt for strong authentication mechanisms.

Strong authentication mechanisms can be implemented using Multi-factor authentication flow by which a number of layers
of authentication will be done for validating the identity of a user. Push based authentication is a mechanism that can
be used as a step in an authentication flow. The flow consists of the user authenticating through a basic authenticator
(username and password) which will be followed by the verification done using a designated app in a mobile device. The
technology used for receiving the authentication request at the mobile app is push notifications. Once the authorization
is provided at the device end, a request will be sent to the Identity Server to authenticate the user into the service
they are attempting to access.




## Components

### Authenticator Component
The push authenticator component is responsible for handling a user’s authentication requests. Once the initial step of basic authentication is completed, the push authenticator gets triggered as the initial component to start a request.

Once the authentication request is initialized at the server end, the request is sent as a push notification to the mobile device. Once the user authorizes or denies the request it’s received at the authentication endpoint and is accepted. A polling mechanism checks regularly to validate if the user has responded to the request. If a record persists, the authentication flow is continued and the user is authenticated to access the required service.

### Device Handler Component
The device handler component is responsible for handling all operations related to devices. All API requests related to
device management are handled by this component. Depending on the request, the component will either create, read,
update or delete records related to devices from the database. Additionally the device handler component is used by the
authenticator component and the servlet component for completing various operations where device management needs to be
done.<br/>
The operations covered by the device handler component are;
- Registering a device
- Unregistering a device
- Getting details of a specific device
- Getting a list of registered devices of a user
- Updating attributes of a device

### Servlet Component
The servlet component handles the API requests related to authentication. All the endpoints called for authentication
are registered at this component and all related operations are completed by it. The operations  covered are;
- Receiving an authentication response from a device and storing the data
- Polling requests for validating if the authentication response has been received

### Common Component
The common component handles functionalities that are required by the authenticator, servlet and API components. The
functionalities handled are;
- Storing authentication context information in cache for the authentication flow
- Validating JWTs sent from mobile devices

### API Components
The API components handle all the API requests received for device management. This works as a bridge between the
authenticated user and the device handler component. While handling requests for the user, it also provides APIs for
admins with the required permissions to address operations related to devices registered under specific users.
<br/><br/>
The support for APIs is handled by two components.
- API common component
    - This contains various constants and utils that are used by the APIs.
      The device handler service used by the APIs is also stored in this component.
- API v1 component
    - This component contains the API stubs and the classes that are used for handling various functionalities of the APIs.
    
## Architecture

Given below is a high level architecture diagram of all the functionalities handled by the push authenticator components
at different levels.

![Push auth high level diagram](https://user-images.githubusercontent.com/19253380/124042699-06f17800-da27-11eb-929c-56b95d332aab.png)



## Design Decisions

- **Using JWT for sending data from mobile device**
    - Data from mobile devices are sent to WSO2 Identity Server through unprotected endpoints. Hence an attacker may be able to intercept requests and feed invalid data.
      By using a JWT, the data can be accessed only by reading the JWT.
      The JWT will be signed by the private key allocated for the given account-device pair and will be verified by WSO2 IS before executing the functionality.
      The JWT is used as a form of authenticating the user by the component.

- **Caching the authentication context after storing JWTs**
    - Authentication requests for push-based authentication are handled by different components for different tasks. Once the polling mechanism identifies that the request has received a response from the mobile device it requests the Authenticator component to authenticate the user.
      Passing the mobile response JWT in a form through the wait page can be insecure and has a potential to be compromised. Hence it is stored in the authentication context to increase security.
      As the authentication flow works in 3 steps (initialize request, receive mobile response, process authentication when the mobile response is received), each step will comprise a new authentication context.
      By adding the authentication context to cache, the stored data from the originally initialized authentication context can be used throughout the entire flow.

- **Polling mechanism**
    - The push-based authentication flow consists of 3 parts at the server end
        - Initializing the authentication request
        - Receiving a response from the mobile device
        - Processing the response once received and authenticating the user
    - As these steps work asynchronously a wait page is displayed and polls the server to verify if the response from
      the mobile device has been received.
    - Once the response has been received and verified, the authentication flow continues to process the request and
      authenticates the user.

- **Using a common component**
    - There are functionalities that are common to multiple components. They are;
        - Authentication cache
        - Validating JWTs
    - Authentication cache stores the authentication context as the same context is updated when the authentication
      response is received for storing the auth response JWT. The authentication context will be accessed through the
      cache in both the Authenticator and Servlet components.
    - JWT validation is done at the Authenticator, Servlet and API components hence, is included in the common component
      to avoid code duplication.

- **Including API components with the other components**
    - The push-based authenticator will not be included in the IS by default.
    - Hence, the API components cannot be compiled into the API webapp through the rest-dispatcher.
    - The API components are included in identity-outbound-auth-push in order to be built and added as dropins to the
      API webapp.
- **Unprotected endpoints**
    - Endpoints for APIs are protected by default in order to allow only authenticated users to access.
    - Since a user session is not maintained by the mobile application, the user authentication cannot be included in
      the requests.
    - Hence, the endpoints for requests from mobile devices are left open and are secured using other mechanisms such as JWTs.

## Security Decisions

- **Use of RSA algorithm for generating keypair**
    - A keypair is generated in the SDK using the RSA algorithm.
    - RSA was used in order to align with the FIDO protocol
    - The key length of the keypair was 1024 as higher key lengths take a longer time to generate in react-native
      applications.

- **Use of JWT**
    - JWTs were used for transferring data from the mobile device to WSO2 IS for authentication requests and remove
      device requests.
    - The data was packed in a JWT signed with a dedicated private key for the account as the data related to these
      requests should be highly secure. In an instance of an attack, the data will not be accessible to cause any harm as a compromised JWT will not be validated by the server.


## Sample Requests
Requests used for authentication are as follows

#### Authentication Request
| POST | https://{host}/push-auth/authenticate |
|------|---------------------------------------|
| Purpose | Authentication request sent from the mobile device |
| Content Type | application/json |
| Request Body |
```javascript
{
    "authResponse" : "eyJhbGciOiJSUzI1NiIsImRpZCI6IjRkZTRlYTk5LTM0ZDMtNGEyYy04YWI4LTcwNDBjZTQ4YzY3ZiJ9.eyJqdGkiOiJlZmVjODAxZi1kM2FhLTRlYzctOTUxMy04N2IzYTVjYWYwNjUiLCJzdWIiOiJhZG1pbkBjYXJib24uc3VwZXIiLCJpc3MiOiJ3c28ydmVyaWZ5IiwiYXVkIjoiaHR0cHM6Ly8xOTIuMTY4LjEuMTEyOjk0NDMvdC9jYXJib24uc3VwZXIvIiwibmJmIjoxNjI0NDM3NTY4LCJleHAiOjE2MjQ0Mzc2ODgsImlhdCI6MTYyNDQzNzU2OCwic2lkIjoiMTVjMjIyMjUtZmExNy00MTRjLTk0MDItNzAzZDMwYTA2MzlkIiwiY2hnIjoiM2M5Mzg2YzYtMjEzMS00NTAwLTk1M2ItZWRmOWVhMjZlYWY4IiwicmVzIjoiU1VDQ0VTU0ZVTCJ9.dUhJqS97rMgy-rVXSg7RaY56Mi9_eE20oXjpqxEVuSggx9aWM5FG3z7yU7f-ylQh5Ca-GbKK79Fibqbnmrv4W7wXKAjROPdnx4fCx8nlEqVYAUtr533LuTbV9Nt-v7UuKVxVWnv_ACEwO8b6aZnngaKiGJL3xIUczlfytwiFjfg"
}
```
| Responses | - |
|-----------|---|
| 201 | Accepted|
| 400 | Bad Request |
| 401 | Unauthorized |
| 500 | Internal Server Error |

#### Check status request
| GET | https://{host}/push-auth/check-status?sessionDataKey={sessionDataKey} |
|-----|----|
| Purpose | Check if the authentication request from the mobile device was received|
|Responses|-|
|200|OK <br/><br/>{<br/>"status": "COMPLETED"<br/>}
|400|Bad Request|
|401|Unauthorized|
|500|Internal Server Error|
