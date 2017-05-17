<%@ page session="false" isELIgnored="false" pageEncoding="UTF-8" contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<html>
<head>
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <meta name="description" content="">
    <meta name="author" content="">
    <title>Registration</title>
</head>
<body>
<div class="container">

    <form:form method="POST" modelAttribute="userForm" class="form-signin" enctype="multipart/form-data">
        <h2 class="form-signin-heading">Create your account</h2>

        <spring:bind path="login">
            <div class="form-group ${status.error ? 'has-error' : ''}">
                <form:input type="text" path="login" class="form-control" placeholder="login"
                            autofocus="true"/>
                <form:errors path="login"/>
            </div>
        </spring:bind>

        <spring:bind path="password">
            <div class="form-group ${status.error ? 'has-error' : ''}">
                <form:input type="password" path="password" class="form-control" placeholder="Password"/>
                <form:errors path="password"/>
            </div>
        </spring:bind>

        <spring:bind path="file">
            <div>
                <form:input type="file" path="file"/>
            </div>
        </spring:bind>

        <button class="btn" type="submit">Submit</button>
    </form:form>

</div>
</body>
</html>
