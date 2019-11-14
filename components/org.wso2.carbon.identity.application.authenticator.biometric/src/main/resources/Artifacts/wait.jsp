<%--
  ~  Copyright (c) 2019 , WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
  ~
  ~  WSO2 Inc. licenses this file to you under the Apache License,
  ~ Version 2.0 (the "License"); you may not use this file except
  ~ in compliance with the License.
  ~  *  You may obtain a copy of the License at
  ~  *
  ~  *  http://www.apache.org/licenses/LICENSE-2.0
  ~  *
  ~  *  Unless required by applicable law or agreed to in writing,
  ~  *  software distributed under the License is distributed on an
  ~  *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
  ~  *  KIND, either express or implied.  See the License for the
  ~  *  specific language governing permissions and limitations
  ~  *  under the License.
  --%>

<%String commonauthURL = "https://biometricauthenticator.private.wso2.com:9443/commonauth";%>

<%@ taglib prefix = "s" uri = "http://java.sun.com/jsp/jstl/core" %>
<%@ page language = "java" contentType = "text/html; charset=UTF-8" pageEncoding = "UTF-8" %>

<html>
<head>
    <meta http-equiv = "X-UA-Compatible" content = "IE=edge">
    <meta charset = "utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>WSO2 Identity Server</title>

    <link rel="icon" href="images/favicon.png" type="image/x-icon"/>
    <link href="libs/bootstrap_3.3.5/css/bootstrap.min.css" rel="stylesheet">
    <link href="css/Roboto.css" rel="stylesheet">
    <link href="css/custom-common.css" rel="stylesheet">
    <link href="css/loading1.css" rel="stylesheet">

    <script language="JavaScript" type="text/javascript" src="libs/jquery_1.11.3/jquery-1.11.3.js"></script>
    <script language="JavaScript" type="text/javascript" src="libs/bootstrap_3.3.5/js/bootstrap.min.js"></script>

    <header class="header header-default">
        <div class="container-fluid"><br></div>
        <div class="container-fluid">
            <div class="pull-left brand float-remove-xs text-center-xs">
                <a href="#">
                    <img src="images/logo-inverse.svg" alt="WSO2" title="WSO2" class="logo">
                    <h1><em>Identity Server</em></h1>
                </a>
                <form id="toCommonAuth" action="<%=commonauthURL%>" method="POST" style="display:none;">
                    <input id="sessionDataKey" type="hidden" name="sessionDataKey">
                    <input id="signedChallenge" type="hidden" name="signedChallenge">
                </form>
            </div>
        </div>
    </header>
</head>
<h2>Please check your mobile device and authenticate with the fingerprint</h2>
<div class="loader"></div>

<script type="text/javascript">
    let sessionDataKey;
    let signedChallenge;
    const refreshInterval = '5000';
    const timeout = '10000';
    const biometricEndpointWithQueryParams = "https://biometricauthenticator.private.wso2.com:9443/samlbiomtriccheck?t=web&SDKWeb=";
    const GET = 'GET';

    $(document).ready(function () {
        const intervalListener = window.setInterval(function () {
            console.log("im here2");
            checkWaitStatus();
        }, refreshInterval);
        let booleanValue = false;

        function checkWaitStatus() {
            const urlParams = new URLSearchParams(window.location.search);
            sessionDataKey = urlParams.get('sessionDataKey');
            console.log("the session data key iss: " + sessionDataKey);

            $.ajax(biometricEndpointWithQueryParams + sessionDataKey, {
                async: false,
                data: {waitingId: sessionDataKey},
                method: GET,
                success: function (res) {
                    booleanValue = true;
                    console.log("res  : " + res);
                    console.log("res status : " + res.status);
                    console.log("res challenge : " + res.signedChallenge);
                    handleStatusResponse(res);
                },
                error: function (res) {
                    checkWaitStatus();
                    console.log("im here6");
                    if (booleanValue === true) {
                        console.log("res is13 : " + res);
                        console.log("res status number is: " + res.status);
                        continueAuthentication();
                    }
                },
                failure: function () {
                    window.clearInterval(intervalListener);
                    console.log("im here7");
                    window.location.replace("/retry.do");
                }
            });
        }

        function handleStatusResponse(res) {
            console.log("boolean Value value is: " + booleanValue);
            if (booleanValue === true) {
                signedChallenge = res.signedChallenge;

                document.getElementById("sessionDataKey").value = sessionDataKey;
                document.getElementById("signedChallenge").value = signedChallenge;
                console.log("res challenge is: " + signedChallenge);
                console.log("im here10");
                continueAuthentication(res);
            }
        }

        function continueAuthentication() {
            console.log("aabbccdd : " + signedChallenge);
            window.clearInterval(intervalListener);
            document.getElementById("toCommonAuth").submit();
        }
    });
</script>
</html>
