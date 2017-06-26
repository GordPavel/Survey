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
    <title>New Survey</title>
    <link href="<c:url value="/pages/css/fonts.css" />" rel="stylesheet">
    <link href="<c:url value="/pages/css/style.css" />" rel="stylesheet">
    <link href="<c:url value="/pages/libs/bootstrap/bootstrap.css" />" rel="stylesheet">
    <link href="<c:url value="/pages/js/needle-1.4.2/lib/auth.js"/>" rel="stylesheet">
    <link href="<c:url value="/pages/js/needle-1.4.2/lib/cookies.js"/>" rel="stylesheet">
    <link href="<c:url value="/pages/js/needle-1.4.2/lib/decoder.js"/>" rel="stylesheet">
    <link href="<c:url value="/pages/js/needle-1.4.2/lib/multipart.js"/>" rel="stylesheet">
    <link href="<c:url value="/pages/js/needle-1.4.2/lib/needle.js"/>" rel="stylesheet">
    <link href="<c:url value="/pages/js/needle-1.4.2/lib/parsers.js"/>" rel="stylesheet">
    <link href="<c:url value="/pages/js/needle-1.4.2/lib/querystring.js"/>" rel="stylesheet">
</head>
<body>
<%-- Изменчивое количество категорий, подсвечивает send , не отправляется--%>
<c:import url="header.jsp"/><div class = "content-bg">
    <div class = "article">
        <p><strong>Новая анкета</strong></p>
    </div>
    <div class="main">
        <form name="test" method="post">
            <b>Введите название анкеты:</b>
            <input id = "nazvanket" type="text" size="40">
            <br>
            <b>Выберите тему анкеты :</b>
            <select id = "kater">
                <c:forEach items="${categories}" var="category">
                    <option id="${category.name}">${category.name}</option>
                </c:forEach>
            </select>
            <br>
            <sec:authentication  property="principal.username" var="login"  scope="request"/>
            <div id="userLogin" style="display: none">${login}</div>
            <div id = "lolkek">
                <b>Название вопроса:</b>
                <input type="text" size="40"> <input type="submit" id = "delq" onclick="deleteqwestion(this)" value="Удалить вопрос" form='send'>
                <br>
                <b>Пропишите ответы на вопрос:(можно выбрать один вариант ответа)</b>
                <br>
                <div id = "poh">
                    <input type="radio"><input type="text" size="40"> <input type="submit" id = "dela" onclick = "deleteotvet(this)" value="Удалить ответ" form='send'>
                    <br>
                </div>
                <input id = "vop" onclick = "newvopr(this)" type="submit" value="Добавить ответ" form='send'>
            </div>
            <input id = "hall" onclick = "newqwection()" type="submit" value="Добавить вопрос" form='send'><br>
            <b>Комментарий:</b> <br>
            <textarea id = "komment" name="comment" cols="40" rows="3"></textarea>
            <br>
            <script language='javascript'>
                var bra = document.getElementById("lolkek");
                var ope = bra.style.display;
                bra.style.display = "none";
                var qwes = new Array();
                var n = 0, chit = 0;

                function newqwection()
                {
                    var kg = document.getElementById("lolkek");
                    var lo = kg.cloneNode(true);
                    lo.style.display = ope;
                    qwes[n] = 0;
                    var poli = "lolkek" + n;
                    lo.id = poli;
                    var lop = lo.getElementsByTagName('input');
                    var otv = lo.getElementsByTagName('div');
                    otv[0].id = "poh" + n + 0;
                    lop[lop.length - 1].id = "vop" + n;
                    lop[lop.length - 2].id = "dela" + n + 0;
                    lop[1].id = "delq" + n;
                    hall.parentNode.insertBefore(lo,hall);
                    ++n;
                    ++chit;
                }

                function newvopr(obj)
                {
                    for(var i = 0; i <= n ; ++i)
                    {
                        if((obj.id == "vop" + i) && (qwes[i] != undefined))
                        {
                            var wer = document.getElementById("poh");
                            var clonewer = wer.cloneNode(true);
                            clonewer.style.display = ope;
                            ++qwes[i];
                            clonewer.id = "poh" + i + qwes[i];
                            var clobut = clonewer.getElementsByTagName('input');
                            clobut[2].id = "dela"  + i + qwes[i];
                            obj.parentNode.insertBefore(clonewer,obj);
                            break;
                        }
                    }
                }

                function deleteqwestion(obj)
                {
                    for(var i = 0; i <= n; ++i)
                    {
                        if((obj.id == "delq" + i) && (qwes[i] != undefined))
                        {
                            var ddd = document.getElementById("lolkek" + i);
                            delete qwes[i];
                            --chit;
                            ddd.parentNode.removeChild(ddd);
                            break;
                        }
                    }
                }

                function deleteotvet(obj)
                {
                    //debugger;
                    for(var i = 0; i <= n; ++i)
                    {
                        var flag = false;
                        if(qwes[i] != undefined)
                        {
                            for(var j = 0; j <= qwes[i]; ++j)
                            {
                                if(obj.id == "dela" + i + j)
                                {
                                    var der = document.getElementById("poh" + i + j);
                                    der.parentNode.removeChild(der);
                                    flag = true;
                                    break;
                                }
                            }
                        }
                        if(flag)
                        {
                            break;
                        }
                    }
                }

                function truestr(str)
                {
                    //debugger;
                    for(var i = 0; i < str.length; ++i)
                    {
                        if(str[i] != ' ')
                        {
                            return false;
                        }
                    }
                    return true;
                    if(str != "")
                    {
                        return false;
                    }
                }

                function Answer(idl, name)
                {
                    this.id = idl;
                    this.name = name;
                }

                function Question(idl, name, answers)
                {
                    this.id = idl;
                    this.name = name;
                    this.answers = answers;
                }

                function Survey(name, comment, questions, madeByUser, category)
                {
                    this.id = null;
                    this.name = name;
                    this.comment = comment;
                    this.answers = null;
                    this.questions = questions;
                    this.creator = madeByUser;
                    this.date = null;
                    this.category = category;
                }

                function User(login)
                {
                    this.name = null;
                    this.lastName = null;
                    this.login = login;
                    this.password = null;
                    this.madeSurveys = null;
                    this.role = null;
                    this.answers = null;
                }
                
                function Category(name) {
                    this.name = name;
                    this.surveys = null;
                }

                function saveanswer()
                {
                    if (chit == 0)
                    {
                        alert("Вы не можете создать опрос без единого вопроса.");
                        return;
                    }
                    else if(truestr(document.getElementById("nazvanket").value))
                    {
                        alert("Вы не ввели название анкеты.");
                        return;
                    }
                    var naza = document.getElementById("nazvanket").value;
                    var str12 = new Array();
                    var kat;
                    var jki = document.getElementById("kater");
                    kat = jki.options[jki.selectedIndex].id;
                    var k = 0;
                    for(var i = 0; i <= n; ++i)
                    {
                        if(qwes[i] != undefined)
                        {
                            var getsqwet = document.getElementById("lolkek" + i);
                            var kekshrek = getsqwet.getElementsByTagName('input');
                            if(truestr(kekshrek[0].value))
                            {
                                alert("Вы не заполнили место для введения вопросов");
                                return;
                            }
                            str12[k] = new Array();
                            str12[k][0] = kekshrek[0].value;
                            var kkfd = 1;
                            var flag1 = true;
                            for(var j = 0; j <= qwes[i]; ++j)
                            {
                                if (document.getElementById("poh" + i + j) != undefined)
                                {
                                    var ff2 = document.getElementById("poh" + i + j);
                                    var braaa = ff2.getElementsByTagName("input");
                                    var kinf = braaa[1].value;
                                    if(truestr(kinf))
                                    {
                                        alert("Вы не заполнели все слоты для ответов");
                                        return;
                                    }
                                    str12[k][kkfd] = kinf;
                                    ++kkfd;
                                    flag1 = false;
                                }
                            }
                            if(flag1)
                            {
                                alert("У каждого вопроса должен быть хотя бы один ответ");
                                return;
                            }
                            ++k;
                        }
                    }
                    var komm = document.getElementById("komment").value;
                    var qwests = new Array();
                    for(var i = 0; i < str12.length; ++i)
                    {
                        var sss = str12[i][0];
                        var ans = new Array();
                        for(var j = 1; j < str12[i].length; ++j) {
                            ans[j - 1] = new Answer(j - 1, str12[i][j]);
                        }
                        qwests[i] = new Question(i, sss, ans);
                    }
                    var logi = new User(document.getElementById("userLogin").innerText);
                    var survey = new Survey(naza, komm, qwests, logi, new Category(kat));
                    var createdSurvey = JSON.stringify(survey);
                    var xmlhttp = new XMLHttpRequest();
                    xmlhttp.open('POST', "/survey/client/createdSurvey", true);
                    xmlhttp.setRequestHeader('Content-Type', 'application/x-www-form-urlencoded');
                    xmlhttp.onreadystatechange = function() {
                        if (xmlhttp.readyState == 4) {
                            if(xmlhttp.status == 200) {
                                document.location.href = "/survey";//тут должна быть ссылка по переходу на статистику
                                return;
                            }
                        }
                    };
                    xmlhttp.send('createdSurvey=' + encodeURIComponent(createdSurvey));
                }
            </script>
        </form>
    </div>
    <div class="sum">
        <input type="submit" value="Сохранить изменения" onclick = "saveanswer()" form='send'>
        <input type="submit" value="Отмена" form='send'>
    </div>
</div>
<c:import url="footer.jsp"/>
</body>
</html>