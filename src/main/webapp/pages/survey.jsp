<%@ page session="false" isELIgnored="false" pageEncoding="UTF-8" contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/security/tags" prefix="sec" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <meta name="description" content="">
    <meta name="author" content="">
    <title>${survey.name}</title>
    <link href="<c:url value="/pages/css/fonts.css" />" rel="stylesheet">
    <link href="<c:url value="/pages/css/style.css" />" rel="stylesheet">
    <link href="<c:url value="/pages/libs/bootstrap/bootstrap.css" />" rel="stylesheet">
</head>
<body>
<c:import url="header.jsp"/>
<div class = "content-bg">
    <div class="article">
        <p><strong> Анкета ${survey.id} ${survey.name}</strong></p> </div>
        <li><a href="<c:url value="/topic?name=${survey.category.name}"/>">Категория ${survey.category.name}</a></li>
        <li><a href="<c:url value="/user?login=${survey.creator.login}"/>">Создатель ${survey.creator.login}</a></li>
    <div class="main">
        <div>
            <c:forEach items="${survey.questions}" var="question">
                <p><b>${question.name}</b></p>
                <div>
                    <c:forEach items="${question.answers}" var="answer">
                        <input type="radio" name="answer" value="a1">${answer.name}<br>
                    </c:forEach>
                </div>
            </c:forEach>
        </div>
    </div>
    <div class="sum">
        <input type="submit" value="Готово" form='send'>
        <input type="reset" value="Очистить">
    </div>
</div>
<c:import url="footer.jsp"/>
</body>
</html>
