<%@ page contentType="text/html; charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page session="false" isELIgnored="false" pageEncoding="UTF-8" contentType="text/html; charset=UTF-8" %>
<!DOCTYPE html>
<head>
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <meta name="description" content="">
    <meta name="author" content="">
    <title>Вход в личный кабинет</title>
    <link href="<c:url value="/pages/css/fonts.css" />" rel="stylesheet">
    <link href="<c:url value="/pages/css/style.css" />" rel="stylesheet">
    <link href="<c:url value="/pages/libs/bootstrap/bootstrap.css" />" rel="stylesheet">
</head>
<body>
<c:import url="header.jsp"/>
<div class="content-bg">
    <h2>Вход в личный кабинет</h2>
    <form action="<c:url value="/j_spring_security_check"/>" class='send' method="post">
        <c:if test="${not empty error}">
            <div style="color: red">${error}</div>
        </c:if>
        <c:if test="${not empty msg}">
            <div class="msg">${msg}</div>
        </c:if>
        <div class="fieldset">
            <label>Логин</label>
            <input type='text' name='login' placeholder='Логин' <c:if test="${not empty error}">style="color: red" </c:if> required autofocus
                   value="s3rius"><span></span>
            <br>
            <label>Пароль</label>
            <input type='password' name='password' placeholder='Пароль' required value="19216211"><span></span>
            <br>
            <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
        </div>
        <div class="sum">
            <input type="submit" value="Войти"/>
            <input type="submit" value="Отмена"/>
        </div>
    </form>
</div>
<c:import url="footer.jsp"/>
</body>
</html>
