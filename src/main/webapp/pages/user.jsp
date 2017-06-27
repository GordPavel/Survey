<%@ page session="false" isELIgnored="false" pageEncoding="UTF-8" contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/security/tags" prefix="sec" %>
<html>
<head>
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <meta name="description" content="">
    <meta name="author" content="">
    <title>${user.login}</title>
    <link href="<c:url value="/pages/css/fonts.css" />" rel="stylesheet">
    <link href="<c:url value="/pages/css/style.css" />" rel="stylesheet">
    <link href="<c:url value="/pages/libs/bootstrap/bootstrap.css" />" rel="stylesheet">
</head>
<body>
<c:import url="header.jsp"/>
<div class="content-bg">
    <div class="article">
        <c:if test="${not empty error}">
            <c:out value="${error}"/>
        </c:if>
        <p><strong>Личный кабинет</strong></p>
        <sec:authentication  property="principal.username" var="login"  scope="request"/>
        <a href="<c:url value="/deleteUser?login=${login}"/>">Удалить профиль</a>
    </div>
    <table cellspacing="0" border="2">
        <tr>
            <th>Созданные анкеты</th>
            <th>Заполненные анкеты</th>
        </tr>
        <tr>
            <td>
                <div class="article">
                    <div class="scroll"><pre>
                        <c:forEach items="${user.madeSurveys}" var="survey">
                            <li><a href="<c:url value="/survey?id=${survey.id}"/>">${survey.name}</a></li>
                        </c:forEach>
 </pre>
                    </div>
                </div>
            </td>
            <td>
                <div class="article">
                    <div class="scroll"><pre>
                        <c:forEach items="${user.answers}" var="userAnswer">
                            <li><a href="<c:url value="/survey?id=${userAnswer.survey.id}"/>">${userAnswer.survey.name}</a></li>
                        </c:forEach>
                      </pre>
                    </div>
                </div>
            </td>
        </tr>
    </table>
</div>
<c:import url="footer.jsp"/>
</body>
</html>
