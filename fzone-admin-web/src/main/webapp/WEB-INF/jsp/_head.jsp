<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" session="false" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<meta charset="utf-8">
<title>${param.title}${param.title != null ? " | " : ""}<spring:eval expression="@config.CO_NAME"/></title>
<c:set var="ver"><spring:eval expression="@config.PUB_DATE"/></c:set>
<c:set var="dev"><spring:eval expression="@config.dev"/></c:set>
<c:if test="${dev eq 'true'}">
<link href="<c:url value="/lib/bootstrap/css/bootstrap.css"/>" rel="stylesheet">
<script src="<c:url value="/lib/jquery/jquery.js"/>"></script>
<script src="<c:url value="/lib/bootstrap/js/bootstrap.js"/>"></script>
</c:if>
<c:if test="!${dev}">
<link href="<c:url value="/lib/bootstrap/css/bootstrap.min.css"/>" rel="stylesheet">
<script src="<c:url value="/lib/jquery/jquery.min.js"/>"></script>
<script src="<c:url value="/lib/bootstrap/js/bootstrap.min.js"/>"></script>
</c:if>
