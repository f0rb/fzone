<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<c:set var="CAPTCHA_IMAGE" value="captcha.png"/>
<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" session="false" %>
<!DOCTYPE HTML>
<html>
<head>
    <c:import url="/WEB-INF/jsp/_head.jsp">
        <c:param name="title"><spring:message code="web.name"/></c:param>
    </c:import>
    <meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no">
    <style type="text/css">
        #formLogin {
            margin:0 auto;
            padding:30px 15px;
            border:1px solid #dbdbdb;
            border-radius:5px;
            color:#444;
            background:-webkit-gradient(linear, left top, left bottom, from(#fefefe), to(#f0f0f0));
            background:-moz-linear-gradient(top, #fefefe, #f0f0f0);
            background:-o-linear-gradient(top, #fefefe, #f0f0f0);
            background:linear-gradient(top, #fefefe, #f0f0f0);
            filter:progid:DXImageTransform.Microsoft.gradient(startColorstr='#fefefe', endColorstr='#f0f0f0');
        }
        header {
            margin:20px;
        }
        @media (min-width:601px) {
            header {
                margin:50px;
            }
            #formLogin {
                width:600px;
            }
        }
    </style>
</head>
<body>
<header style="text-align: center">
    <h1></h1>
</header>
<form id="formLogin" method="post" autocomplete="off" class="form-horizontal" action="<c:url value="/login"/>">
    <c:if test='${user.messages != null}'>
        <div class="form-group">
            <p class="col-sm-offset-4 col-sm-6 text-danger">${user.messages.login[0]}</p>
        </div>
    </c:if>
    <input name="redirect" type="hidden" value="${param.redirect}">
    <div class="form-group">
        <label class="control-label col-sm-4 " for=inputUsername><spring:message code="user.username.label"/>${dev}</label>
        <div class="col-sm-6">
            <input id="inputUsername" name="username" class="form-control" type="text" required>
        </div>
    </div>
    <div class="form-group">
        <label class="control-label col-sm-4" for="inputPassword"><spring:message code="user.password.label"/></label>
        <div class="col-sm-6">
            <input id="inputPassword" name="password" class="form-control" type="password" required>
        </div>
    </div>
    <div class="form-group">
        <label class="control-label col-sm-4" for=inputCaptcha><spring:message code="captcha"/></label>
        <div class="col-sm-6">
            <input id=inputCaptcha name='captcha' class="form-control" type=text autocomplete=off required>
            <span class="help-block">
                    <img src="<c:url value="/${CAPTCHA_IMAGE}"/>"
                         alt="<spring:message code="captcha"/>" title="<spring:message code="captcha.title"/>"
                         style="cursor:pointer;width:80px;height:20px;"
                         onclick="$(this).attr('src', '<c:url value="/${CAPTCHA_IMAGE}"/>?r=' + Math.random())">
                </span>
        </div>
    </div>
    <div class="form-group">
        <div class="col-sm-offset-4 col-sm-6">
            <button type=submit class="btn btn-default">
                <spring:message code="login.submit"/>
            </button>
        </div>
    </div>
</form>
</body>
</html>