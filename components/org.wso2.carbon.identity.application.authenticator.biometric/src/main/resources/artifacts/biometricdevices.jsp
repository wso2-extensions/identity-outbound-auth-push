<%--
  ~ Copyright (c) 2020, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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
  --%>
<%@ page import="org.json.simple.JSONArray" %>
<%@ page import="org.json.simple.JSONObject" %>
<%@ page import="org.json.simple.parser.JSONParser" %>
<%@ page import="org.wso2.carbon.identity.application.authenticator.biometric.device.handler.model.Device" %>
<%@ page import="java.nio.charset.StandardCharsets" %>
<%@ page import="java.util.ArrayList" %>

<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%
    ArrayList<Device> xx = new ArrayList<>();
    Device device1;
    JSONParser parser = new JSONParser();
    JSONArray devicearray = (JSONArray) parser.parse(request.getParameter("devices"));
    for (int i = 0; i < devicearray.size(); i++) {
        JSONObject obj = (JSONObject) devicearray.get(i);
        device1 = new Device();
        device1.setDeviceId((String) obj.get("deviceId"));
        device1.setDeviceName((String) obj.get("deviceName"));
        device1.setDeviceModel((String) obj.get("deviceModel"));
        device1.setPushId((String) obj.get("lastUsedTime"));
        xx.add(device1);
    }
    pageContext.setAttribute("DEVICE_LIST", xx);
    request.setAttribute("sessionDataKey", request.getParameter("sessionDataKey"));
%>
<!DOCTYPE html>
<html>

<head>
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>WSO2 Identity Server</title>
    <meta charset="<%=StandardCharsets.UTF_8.name()%>">

    <link rel="icon" href="images/favicon.png" type="image/x-icon"/>
    <link href="libs/bootstrap_3.4.1/css/bootstrap.min.css" rel="stylesheet">
    <link href="css/Roboto.css" rel="stylesheet">
    <link href="css/custom-common.css" rel="stylesheet">
    <link href="css/devicespage.css" rel="stylesheet">


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
<h1>Pick a Device</h1>
<div class="container" class="grid-container">
    <c:forEach var="device" items="${pageScope.DEVICE_LIST}">
        <div class="grid-item">
            <div class="card" style="width: 60rem;">
                <div class="card-body">
                    <h3 class="card-title">${device.getDeviceName()}</h3>
                    <p class="card-text"><strong>Model :  </strong>${device.getDeviceModel()}</p>
                    <p class="card-text"><strong>Last used on : </strong>${device.getPushId()}</p>
                    <form style="align-items: center" action="/biometric-auth" method="GET">
                        <input type="hidden" name="ACTION" value="Authenticate"/>
                        <input type="hidden" name="sessionDataKey" value=<%=request.getParameter("sessionDataKey")%>>
                        <button type="submit" name="deviceId" value="${device.getDeviceId()}" class="btn btn-primary">Proceed</button>
                    </form>
                </div>
            </div>
        </div>
    </c:forEach>
</div>

</body>
</html>


