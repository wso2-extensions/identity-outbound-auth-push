

<html>
<head>
    <title>Testing QR code</title>
    <div style="text-align: center;"><h1> Scan the QR code with your mobile device</h1></div>
    <script src="https://ajax.googleapis.com/ajax/libs/jquery/1.11.2/jquery.min.js"></script>

</head>
<body>

<%--<div style="text-align: center;"><h2>now in the body section</h2></div>--%>


<%--       value="Scan the QR code with your mobile app " style="Width:20%"--%>


<img
     id="qr"
     alt=""
     title="HELLO"
     width="250"
     height="250"/>
<script type="text/javascript">


    function generateBarCode()
    {
        let endpoint_url="https://biometricauthenticator.private.wso2.com:9443/samlbiomtriccheck";
        let myname="yasara";
        let mychalenge = "qqqqqrrrrssstttt";
        let dataUrl = "https://api.qrserver.com/v1/create-qr-code/?data=HelloWorld&amp;size=500x500";

        // data = new JSON();
        // data.put("message", message);
        // let endpointURL;
        var myData= myname+"+"+endpoint_url+"+"+mychalenge;
        console.log("hi there : "+ myData.toString());
        var url = 'https://api.qrserver.com/v1/create-qr-code/?data=' + myData + '&amp;size=250x250';
        //dataUrl=url;
        //$('#barcode').attr('src', url);
        document.getElementById("qr").src = url;
    }
</script>
<div style="text-align: center;"><script type="text/javascript">generateBarCode()</script></div>
</body>
</html>


<%--<html>--%>
<%--<head>--%>
<%--    <meta http-equiv="X-UA-Compatible" content="IE=edge">--%>
<%--    <meta charset="utf-8">--%>
<%--    <meta name="viewport" content="width=device-width, initial-scale=1.0">--%>
<%--    <title>WSO2 Identity Server</title>--%>

<%--    <link rel="icon" href="images/favicon.png" type="image/x-icon"/>--%>
<%--    <link href="libs/bootstrap_3.3.5/css/bootstrap.min.css" rel="stylesheet">--%>
<%--    <link href="css/Roboto.css" rel="stylesheet">--%>
<%--    <link href="css/custom-common.css" rel="stylesheet">--%>

<%--    <!-- header -->--%>
<%--    <header class="header header-default">--%>
<%--        <div class="container-fluid"><br></div>--%>
<%--        <div class="container-fluid">--%>
<%--            <div class="pull-left brand float-remove-xs text-center-xs">--%>
<%--                <a href="#">--%>
<%--                    <img src="images/logo-inverse.svg" alt="WSO2" title="WSO2" class="logo">--%>

<%--                    <h1><em>Identity Server</em></h1>--%>

<%--                </a>--%>
<%--            </div>--%>
<%--        </div>--%>
<%--    </header>--%>
<%--</head>--%>
<%--    <h1>Success!!!!!!!!!!!!</h1>--%>

<%--&lt;%&ndash;    <h2>Hello <%request.getAttribute("challenge");%></h2>&ndash;%&gt;--%>


<%--</html>--%>
