<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Sign Service</title>
<script type="text/javascript" src="resources/scripts/signering.js"></script>
</head>
  <body onload="startSign(${signData.tbs}, ${signData.tbs}, ${signData.nonce})">
    <form method="POST" name="signerData" action="./verify">
      <input type="hidden" name="SignedData" value="" />
      <input type="hidden" name="submitUri" value="${signData.submitUri}" />
      <input type="hidden" name="tbs" value="${signData.tbs}" />
    </form>
  </body>
</html>