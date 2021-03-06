<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>Signera</title>

    <style type="text/css">
        @import 'resources/styles/form.css';
    </style>

    <script type="text/javascript" src="https://ajax.aspnetcdn.com/ajax/jQuery/jquery-1.6.1.min.js"></script>
    <script type="text/javascript" src="resources/scripts/form-effects.js"></script>
</head>
<body>
<h2>Signera med E-legitimation från:</h2>

<div id="selectClientTypeWrapper">
    <form:form commandName="signData" id="sign-selection" action="prepare" method="post">
        <fieldset>
            <legend>E-legitimationer</legend>
            <ul id="e-leg-types">
                <c:forEach items="${clientTypes}" var="clientType">
                    <li class="e-leg-type">
                        <form:radiobutton path="clientType" id="clientType_${clientType.id}" value="${clientType.id}"
                                          cssClass="radio" label="${clientType.name}"/>
                        <div class="e-leg-type-description">${clientType.description}</div>
                    </li>
                </c:forEach>
            </ul>
        </fieldset>
        <fieldset class="submit">
            <input type="submit" value="Signera" class="submit"/>
        </fieldset>
        <form:hidden id="tbs" path="tbs"/>
        <form:hidden id="submitUri" path="submitUri"/>
        <form:hidden id="ticket" path="ticket"/>
    </form:form>
</div>
</body>
</html>
