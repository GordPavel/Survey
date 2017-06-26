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
    <script>
for(var i = 0; i < 5/*колчество вопросов*/; ++i)
{
var nediv = document.createElement("div");
var fre = document.createElement("canvas");
fre.id = "popChart" + i;
fre.width = 300;
fre.height = 200;
nediv.appendChild(fre);
ok.parentNode.insertBefore(nediv,ok);
var popCanvas = $("#popChart" + i);
var popCanvas = document.getElementById("popChart" + i);
var popCanvas = document.getElementById("popChart" + i).getContext("2d");
var mass = ["China", "India", "United States", "Indonesia", "Brazil"]; 
var mass1 = [1379302771, 1281935911, 326625791, 260580739, 207353391];
     
var barChart = new Chart(popCanvas, {
  type: 'pie',
  data: {
    labels: mass,
      
    datasets: [{
      label: 'Population',
      data: mass1,
        strokeColor: "rgba(220, 220, 220, 0.92)",
      backgroundColor: [
        '#FE6DA8',
        '#56B7F1',
        '#CDA67F',
        '#FED70E"',
        'rgba(153, 102, 255, 0.6)'
      ]
    }]
  }
});
}

  </script>
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
            </c:forEach>
        </div>
    </div>
    <div class="sum">
        <input type="submit" value="Готово" onclick="saveall()" form='send'>
        <input type="reset" onclick="deleteall()" value="Очистить">
    </div>
</div>
<c:import url="footer.jsp"/>
</body>
</html>
