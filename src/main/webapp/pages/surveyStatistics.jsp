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
    <script src="https://cdnjs.cloudflare.com/ajax/libs/Chart.js/2.6.0/Chart.js"></script>
    <script src="https://ajax.googleapis.com/ajax/libs/jquery/3.2.1/jquery.min.js"></script>
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
                <br>${question.name}.
                <c:if test="${question.userAnsweredOn != null}">Ваш ответ "<b>${question.userAnsweredOn.name}</b>"</c:if>
                <div id="statistics${question.id}"></div>
            </c:forEach>
        </div>
    </div>
    <div class="sum">
        <input type="submit" id ="ok" value="Готово" onclick="saveall()" form='send'>
    </div>
    <script>
        var sss ='${statistics}';
        var sssr = JSON.parse(sss);
        var qwestions = sssr.questions;
        for (var i = 0; i < qwestions.length; ++i) {
            var nediv = document.createElement("div");
            var fre = document.createElement("canvas");
            fre.id = "popChart" + i;
            fre.width = 100;
            fre.height = 50;
            nediv.appendChild(fre);
            var der = document.getElementById("statistics" + i);
            der.parentNode.insertBefore(nediv, der.nextSibling);
            var popCanvas = $("#popChart" + i);
            var popCanvas = document.getElementById("popChart" + i);
            popCanvas.width = 100;
            popCanvas.height = 50;
            var popCanvas = document.getElementById("popChart" + i).getContext("2d");
            var pipiska = qwestions[i].answers;
            var mass = new Array();
            var mass1 = new Array();
            var d ;
            for(var j = 0; j < pipiska.length; ++j)
            {
                mass[j] = pipiska[j].name;
                mass1[j] = pipiska[j].usersAnswered;
                d = j;
            }
            mass[d + 1] = "Прочее";
            mass1[d + 1] = qwestions[i].other;


            var barChart = new Chart(popCanvas, {
                type: 'doughnut',
                data: {
                    labels: mass,
                    datasets: [{
                        label: 'Population',
                        data: mass1,
                        strokeStyle: '#fffbfb',
                        backgroundColor: [
                            '#FE6DA8',
                            '#56B7F1',
                            '#CDA67F',
                            'rgba(153, 102, 255, 0.6)',
                            '#8d8d8d'
                        ]
                    }]
                },
                options:{
                    legend: {
                        labels: {
                            fontColor: 'black'
                        }
                    }

                }
            });

        }
        
        function saveall() {
            document.location.href = "/survey";
        }

    </script>
</div>
<c:import url="footer.jsp"/>
</body>
</html>
