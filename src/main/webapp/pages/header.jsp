<%@ page contentType="text/html; charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/security/tags" prefix="sec" %>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<!DOCTYPE html>
<head>
    <link href="<c:url value="/pages/css/fonts.css" />" rel="stylesheet">
    <link href="<c:url value="/pages/css/style.css" />" rel="stylesheet">
    <link href="<c:url value="/pages/libs/bootstrap/bootstrap.css" />" rel="stylesheet">
</head>
<body>
<div class="header">
    <div class="menu">
        <ul>
            <li><a href="<c:url value="/"/>"><spring:message code="main"/></a></li>
            <li><a href="<c:url value="/topics"/>"> <spring:message code="themes"/> </a></li>
            <li><a href="<c:url value="/info"/>"> <spring:message code="about"/> </a></li>
        </ul>
    </div>
    <div class="search">
        <form action="<c:url value="/search"/>" method="post">
            <input type="text" name="search" placeholder="Поиск"/>
        </form>
        <img src="<c:url value="/pages/images/search.png"/>">
    </div>
    <div class="reg">
        <sec:authorize access="!isAuthenticated()">
            <a class="button" href="<c:url value="/login" />">Войти</a><a>/</a></a><a class="button" href="<c:url value="/registration"/>" role="button">Регистрация</a>
        </sec:authorize>
        <sec:authorize access="isAuthenticated()">
            <p><a class="button" href="<c:url value="/logout"/>" role="button">Выйти</a></p>
        </sec:authorize>
    </div>
</div>
</body>
</html>
