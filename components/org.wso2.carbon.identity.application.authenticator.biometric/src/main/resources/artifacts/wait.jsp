<%--
  ~ Copyright (c) 2019, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
  ~
  ~ WSO2 Inc. licenses this file to you under the Apache License,
  ~ Version 2.0 (the "License"); you may not use this file except
  ~ in compliance with the License.
  ~ You may obtain a copy of the License at
  ~
  ~ http://www.apache.org/licenses/LICENSE-2.0
  ~
  ~ Unless required by applicable law or agreed to in writing,
  ~ software distributed under the License is distributed on an
  ~ "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
  ~ KIND, either express or implied.  See the License for the
  ~ specific language governing permissions and limitations
  ~ under the License.
  ~
  --%>

<%String commonauthURL = "https://192.168.1.6:9443/commonauth";%>

<%@ taglib prefix = "s" uri = "http://java.sun.com/jsp/jstl/core" %>
<%@ page language = "java" contentType = "text/html; charset=UTF-8" pageEncoding = "UTF-8" %>
<%@ page import="org.wso2.carbon.identity.application.authenticator.biometric.BiometricAuthenticatorConstants" %>
<%@ page import="java.nio.charset.StandardCharsets" %>

<html>
<head>
    <meta http-equiv = "X-UA-Compatible" content = "IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>WSO2 Identity Server</title>
    <meta charset="<%=StandardCharsets.UTF_8.name()%>">

    <link rel="icon" href="images/favicon.png" type="image/x-icon"/>
    <link href="libs/bootstrap_3.3.5/css/bootstrap.min.css" rel="stylesheet">
    <link href="css/Roboto.css" rel="stylesheet">
    <link href="css/custom-common.css" rel="stylesheet">
    <link href="css/loading1.css" rel="stylesheet">

    <script language="JavaScript" type="text/javascript" src="libs/jquery_3.4.1/jquery-3.4.1.js"></script>
    <script language="JavaScript" type="text/javascript" src="libs/bootstrap_3.4.1/js/bootstrap.min.js"></script>

    <header class="header header-default">
        <div class="container-fluid"><br></div>
        <div class="container-fluid">
            <div class="pull-left brand float-remove-xs text-center-xs">
                <a href="#">
                    <img src="images/logo-inverse.svg" alt="WSO2" title="WSO2" class="logo">
                    <h1><em>Identity Server</em></h1>
                </a>
            </div>
        </div>
    </header>
</head>
<body>
<h2>Please check your mobile device and authenticate with the fingerprint</h2>
<div class="loader"></div>
<form id="toCommonAuth" action="<%=commonauthURL%>" method="POST" style="display:none;">
    <input type="hidden" name="ACTION" value="WaitResponse"/>
    <label for="sessionDataKey">sessionDataKey</label><input id="sessionDataKey" name="sessionDataKey">
    <label for="signedChallenge">signedChallenge</label><input id="signedChallenge" name="signedChallenge">
</form>
</body>
<script type="text/javascript">
    let i = 0;
    let sessionDataKey;
    let signedChallenge;
    const refreshInterval = 1000;
    const timeout = 50000;
    let biometricEndpointWithQueryParams = "<%=BiometricAuthenticatorConstants.BIOMETRIC_ENDPOINT +
     BiometricAuthenticatorConstants.POLLING_QUERY_PARAMS%>";
    const GET = 'GET';

    $(document).ready(function () {
        var startTime = new Date().getTime();
        console.log("Start time: "+ startTime);

        const intervalListener = window.setInterval(function () {
            checkWaitStatus();
            i++
            console.log("Polled ${i} times")
        }, refreshInterval);

        function checkWaitStatus() {
            const now = new Date().getTime();
            if ((startTime + timeout) < now) {
                alert("timeout triggered");
                window.clearInterval(intervalListener);
                window.location.replace("retry.do");
            }

            const urlParams = new URLSearchParams(window.location.search);
            sessionDataKey = urlParams.get('sessionDataKey');

            $.ajax(biometricEndpointWithQueryParams + sessionDataKey, {
                async: false,
                method: GET,
                // todo: check whether there are any problems when same get req is sent over n over again, will there be cache issues? any solutions?-Future Improvement-Include in DOCs
                success: function (res) {

                    handleStatusResponse(res);
                },
                error: function () {

                    checkWaitStatus();
                },
                failure: function () {

                    window.clearInterval(intervalListener);
                    window.location.replace("/retry.do");
                }
            });
        }

        function handleStatusResponse(res) {

            if ((res.status) === "<%=BiometricAuthenticatorConstants.COMPLETED%>") {
                signedChallenge = res.signedChallenge;
                document.getElementById("sessionDataKey").value = sessionDataKey;
                document.getElementById("signedChallenge").value = signedChallenge;
                continueAuthentication(res);
            } else {
                checkWaitStatus();
            }
        }

        function continueAuthentication() {

            window.clearInterval(intervalListener);
            document.getElementById("toCommonAuth").submit();
            console.log("Polling")
        }
    });
</script>
</html>
