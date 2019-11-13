<%@ page import="org.owasp.encoder.Encode" %>
<% String successURL = "https://biometricauthenticator.private.wso2.com:9443/authenticationendpoint/success.do"; %>
<% String waitURL = "https://biometricauthenticator.private.wso2.com:9443/authenticationendpoint/wait.do"; %>
<%String commonauthURL="https://biometricauthenticator.private.wso2.com:9443/commonauth";%>

<%@ taglib prefix="s" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>

<html>
<head>
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>WSO2 Identity Server</title>

    <link rel="icon" href="images/favicon.png" type="image/x-icon"/>
    <link href="libs/bootstrap_3.3.5/css/bootstrap.min.css" rel="stylesheet">
    <link href="css/Roboto.css" rel="stylesheet">
    <link href="css/custom-common.css" rel="stylesheet">
    <link href="css/loading1.css" rel="stylesheet">

    <script language="JavaScript" type="text/javascript" src="libs/jquery_1.11.3/jquery-1.11.3.js"></script>
    <script language="JavaScript" type="text/javascript" src="libs/bootstrap_3.3.5/js/bootstrap.min.js"></script>

    <!-- header -->
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
<%--                <form id="toSuccessPage" action="<%=successURL%>" method="POST" style="display:none;">--%>
<%--
                    </form>
                <form id="toWaitPage" action="<%=waitURL%>" method="POST" style="display:none;">
                </form>
            </div>
        </div>
    </header>
</head>
<h1>Please check your mobile device</h1>
<%--<h2><%request.getAttribute("challenge");%></h2>--%>

<script type="text/javascript">
    var sessionDataKey;
    var signedChallenge;
    var refreshInterval = '5000';
    var timeout = '10000';


    $(document).ready(function () {
        console.log("im here1");
        var intervalListener = window.setInterval(function () {
            console.log("im here2");
            checkWaitStatus();
        }, refreshInterval);
        console.log("im here at 2.1");
        // var timeoutListenerListener = window.setTimeout(function () {
        //     console.log("im here3");
        //     window.clearInterval(intervalListener);
        //     window.location.replace("/error.do");
        // }, timeout);
        var v1=false;
        function checkWaitStatus() {
            console.log("im here4");
            const urlParams = new URLSearchParams(window.location.search);
            sessionDataKey = urlParams.get('sdk');
            console.log("the sdk iss: "+ sessionDataKey);

            $.ajax("https://biometricauthenticator.private.wso2.com:9443/samlbiomtriccheck?t=web&SDKWeb="+sessionDataKey, {
                async: false,
                data: {waitingId: sessionDataKey },
                //crossDomain: true,
                //url: "https://immense-depths-6983.herokuapp.com/search?search=94305",
                method: "GET",
                // headers: {
                //     "authorization": "Basic YWRtaW46cGFzc3dvcmQ=",
                //     "cache-control": "no-cache",
                //     "postman-token": "54733c33-4918-811b-3987-69d6edeaa3a0"
                // },
                success: function (res) {
                    v1=true;
                    console.log("im here5");
                    console.log("res  : " + res);
                    console.log("res status : " + res.status);
                    console.log("res challenge : " + res.signedChallenge);
                    //console.log("chhchc: "+ response.values("status","challenge"));

                    //console.log("finally the challenge :: "+request.getAttribute("challenge"));
                    handleStatusResponse(res);
                },
                error: function (res) {
                    checkWaitStatus();
                    //window.clearInterval(intervalListener);
                    console.log("im here6");
                    if (v1 === true ) {
                        console.log("res is13 : " + res);
                        console.log("res status number is: "+ res.status);
                        //console.log("finally the challenge :: "+request.getAttribute("challenge"));
                        //console.log(res.getChallenge());
                        console.log("im here 6.1");
                        continueAuthentication();
                    }
                    // else{
                    //     document.getElementById("toWaitPage").innerHTML=("error 0.1: "+response);
                    // }
                    //window.location.replace("/retry.do");
                },
                failure: function (res) {
                    window.clearInterval(intervalListener);
                    console.log("im here7");
                    window.location.replace("/retry.do");
                }
            });
        }
        console.log("im here8");
        function handleStatusResponse(res) {
            console.log("im here9");
            console.log("V1 value is: " + v1);

            //if (res.getStatus() === 200) {
            if (v1 === true ) {
                signedChallenge= res.signedChallenge;

                document.getElementById("sessionDataKey").value = sessionDataKey;
                document.getElementById("signedChallenge").value = signedChallenge;
                console.log("res challenge is: "+ signedChallenge);
                console.log("im here10");
                continueAuthentication(res);
            }

        }
        function continueAuthentication(res) {
            console.log("im here11");
            console.log("aabbccdd : "+ signedChallenge);
            window.clearInterval(intervalListener);
            document.getElementById("toCommonAuth").submit();

        }
    });

</script>

</html>




