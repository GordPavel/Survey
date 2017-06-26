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
            <sec:authentication  property="principal.username" var="login"  scope="request"/>
            <c:set var="i" value="0"/>
            <c:forEach items="${survey.questions}" var="question">
                <p><b>${question.name}</b></p>
                <div>
                    <c:forEach items="${question.answers}" var="answer">
                        <input type="radio" name="answer${i}" value="a1"> ${answer.name}<br>
                    </c:forEach>
                </div>
                <c:set var="i" value="${i+1}"/>
            </c:forEach>
        </div>
        <div id="qweslenght" style="display: none">${i}</div>
        <div id="userLogin" style="display: none">${login}</div>
    </div>
    <div class="sum">
        <input type="submit" value="Готово" onclick="saveall()" form='send'>
        <input type="reset" onclick="deleteall()" value="Очистить">
    </div>
</div>
<script language = 'javascript'>
    function saveall()
    {
        var trueotvets = new Array();
        var valanswer = parseInt((document.getElementById("qweslenght").innerText), 10);
        for(var i = 0; i < valanswer; ++i)
        {
            var kekerrt = document.getElementsByName("answer" + i);
            var flag = true;
            for(var j = 0; j < kekerrt.length; ++j)
            {
                if(kekerrt[j].checked == "1")
                {
                    trueotvets[i] = j;
                    flag = false;
                    break;
                }
            }
            if (flag)
            {
                alert("Вы не ответили на все вопросы");
                return;
            }
        }
        var answers = JSON.stringify(trueotvets);
        var id = ${survey.id};
        var login = document.getElementById("userLogin").innerText; //надо получить через проперти
        var xmlhttp = new XMLHttpRequest();
        xmlhttp.open('POST', "/survey/client/doneSurvey"/*здесь вставить адрес сервера для отправки*/, true);
        xmlhttp.setRequestHeader('Content-Type', 'application/x-www-form-urlencoded');
        xmlhttp.onreadystatechange = function() {
            if (xmlhttp.readyState == 4) {
                if(xmlhttp.status == 200) {
                    document.location.href = "/survey/survey?id=" + id;//тут должна быть ссылка по переходу на статистику
                    return;
                }
            }
        };
        xmlhttp.send('answers=' + encodeURIComponent(answers)+ '&id=' + encodeURIComponent(id) + '&login=' + encodeURIComponent(login));
    }

    function deleteall()
    {
        var valanswer = parseInt((document.getElementById("qweslenght").innerText), 10);
        for(var i = 0; i < valanswer; ++i)
        {
            var kekerrt = document.getElementsByName("answer" + i);
            for(var j = 0; j < kekerrt.length; ++j)
            {
                if(kekerrt[j].checked == "1")
                {
                    //debugger;
                    kekerrt[j].checked = false;
                }
            }
        }
    }

</script>
<c:import url="footer.jsp"/>
</body>
</html>
