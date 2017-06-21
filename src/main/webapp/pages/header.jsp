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
    <div class = "header">
        <div class  = "menu">
            <a href="<c:url value="/" />"><img src="<c:url value="/pages/images/logo.png"/>"></a>
            <ul>
                <li><a href="<c:url value="/"/>"><spring:message code="main"/></a></li>
                <li><a href="<c:url value="/topics"/>"> <spring:message code="themes"/> </a></li>
                <li><a href="<c:url value="/info"/>"> <spring:message code="about"/> </a></li>
            </ul>
        </div>
        <!--ПОИСК-->
        <div class = "search">
            <form action="#" method="post">
                <input  type="text" name ="search" placeholder="Поиск "/>
            </form>
            <a href="search.html" ><img src="<c:url value="/pages/images/search.png"/>" ></a>
        </div>
        <!--регистрация-->
        <div class = "reg">
            <form action = "#" method = "form-inline">
                <sec:authorize access="!isAuthenticated()">
                    <a class="button" href="<c:url value="/login" />">Войти</a>
                    <a>/</a>
                    <a class="button" href="<c:url value="/registration"/>" role="button">Регистрация</a>
                    <a>/</a>
                </sec:authorize>
                <sec:authorize access="isAuthenticated()">
                    <a href="<c:url value="/newSurvey"/>" class="button">Создать анкету</a>
                    <a>/</a>
                    <a class="button" href="<c:url value="/logout"/>" role="button">Выйти</a>
                </sec:authorize>
            </form>
        </div>
        <sec:authorize access="isAuthenticated()">
            <sec:authentication  property="principal.username" var="login"  scope="request"  />
            <div class="btn-group">
                <b><a href="<c:url value="/user?login=${login}"/>">${login}</a></b>
                <img class="top_profile_img"
                     src="<c:url value="/client/img?id=${login}"/>"
                     border-radius="25px" alt="login">
            </div>
        </sec:authorize>
    </div>
</div>
</body>
</html>


