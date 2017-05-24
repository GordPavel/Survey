<%@ page session="false" isELIgnored="false" pageEncoding="UTF-8" contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<head>
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <meta name="description" content="">
    <meta name="author" content="">
    <title>Registration</title>
    <link href="<c:url value="/pages/css/fonts.css" />" rel="stylesheet">
    <link href="<c:url value="/pages/css/style.css" />" rel="stylesheet">
    <link href="<c:url value="/pages/libs/bootstrap/bootstrap.css" />" rel="stylesheet">
</head>
<body>

<c:import url="header.jsp"/>

<div class="content-bg">

    <h2>Регистрация пользователя</h2>
    <form:form modelAttribute="userForm" enctype="multipart/form-data" class='send' method="post">
        <div class="fieldset">
            <label>Имя</label>
            <form:input type="text" path="name" class="form-control" placeholder="Name"/><span></span>
            <form:errors path="name"/>
            <br>
            <label>Фамилия</label>
            <form:input type="text" path="lastName" class="form-control" placeholder="Last Name"/><span></span>
            <form:errors path="lastName"/>
            <br>
            <label>Логин</label>
            <spring:bind path="login">
                <div class="form-group ${status.error ? 'has-error' : ''}">
                    <form:input type="text" path="login" class="form-control" placeholder="login"
                                autofocus="true"/><span></span>
                    <form:errors path="login"/>
                </div>
            </spring:bind>
            <br>
        </div>
        <div class='fieldset'>
            <label>Пароль</label>
            <spring:bind path="password">
                <div class="form-group ${status.error ? 'has-error' : ''}">
                    <form:input type="password" path="password" class="form-control"
                                placeholder="Password"/><span></span>
                    <form:errors path="password"/>
                </div>
            </spring:bind>
            <br>
            <label>Повторите пароль</label>
            <spring:bind path="passwordRepeat">
                <div class="form-group ${status.error ? 'has-error' : ''}">
                    <form:input type="password" path="passwordRepeat" class="form-control"
                                placeholder="Repeat password"/><span></span>
                    <form:errors path="passwordRepeat"/>
                </div>
            </spring:bind>
            <br>
            <label>Фотография</label>
            <spring:bind path="file">
                <div>
                    <form:input type="file" class="form-control" path="file"/><span></span>
                    <form:errors path="file"/>
                </div>
            </spring:bind>
        </div>
        <div class="sum">
            <input type="submit" value="Зарегистрироваться">
            <input type="button" href="<c:url value="/"/>" value="Отмена">
            <input type="reset" value="Очистить">
        </div>
    </form:form>
</div>
<c:import url="footer.jsp"/>
</body>
</html>
