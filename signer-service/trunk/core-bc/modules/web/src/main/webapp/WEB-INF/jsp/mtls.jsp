<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>

<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge"/>
    <title>Signering - Certifikat</title>
    <script type="text/javascript" src="https://ajax.aspnetcdn.com/ajax/jQuery/jquery-1.6.1.min.js"></script>
    <style type="text/css">
        @import 'resources/styles/form.css';
    </style>
</head>

<body style="font-family: Helvetica, Arial, sans-serif; font-size: 16px">

<form action="${postbackUrl}" method="post">
<%--    <input type="hidden" name="DataToBeSigned" value="${signData.encodedTbs}"/>--%>
    <input type="hidden" name="encodedTbs" value="${signData.encodedTbs}"/>
    <input type="hidden" name="submitUri" value="${signData.submitUri}"/>
    <input type="hidden" name="clientType" value="${signData.clientType.id}"/>
    <input type="hidden" name="ticket" value="${ticket}"/>

    <div style="display: block; max-width: 400px; padding: 0 12px">
        <p><strong>Jag signerar:</strong></p>
        <p>${signData.tbs}</p>
        <input style="margin-top: 12px; padding: 6px; font-size: 1em" type="submit" value="Signera beställning"/>
        <p style="color: red">OBS! din pinkod sparas efter första signeringen. Vid efterföljande signeringar under samma session behöver du inte ange din pinkod igen.</p>
    </div>
</form>
<%--
<input type="hidden" name="DataToBeSigned" value="${signData.encodedTbs}"/>
<input type="hidden" name="PostURL"
       value="${postbackUrl}/verify?submitUri=${signData.submitUri}&clientType=${signData.clientType.id}"/>
<form id="cancel-form" method="post" action="cancel">
    <input type="hidden" name="submitUri" value="${signData.submitUri}"/>
    <input type="hidden" name="clientType" value="${signData.clientType.id}"/>
    <input type="hidden" id="errorCode" name="errorCode"/>
</form>--%>


</body>

</html>
