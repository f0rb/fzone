<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" session="false" %>
<!DOCTYPE html>
<html ng-app="GRSApp">
<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no">
    <title>GRS - 后台管理系统</title>
    <base href="${pageContext.request.contextPath}/">

    <link rel="stylesheet" href="<c:url value="/lib/font-awesome/css/font-awesome.min.css"/>">

    <script src="<c:url value="/lib/jquery/jquery.min.js"/>"></script>

    <link rel="stylesheet" href="<c:url value="/lib/bootstrap/css/bootstrap.min.css"/>">
    <script src="<c:url value="/lib/bootstrap/js/bootstrap.min.js"/>"></script>

    <script src="<c:url value="/lib/angular/angular.min.js"/>"></script>
    <script src="<c:url value="/lib/angular/angular-ui-router.min.js"/>"></script>
    <script src="<c:url value="/lib/angular/ct-ui-router-extras.min.js"/>"></script>
    <script src="<c:url value="/lib/angular/angular-resource.min.js"/>"></script>
    <script src="<c:url value="/lib/angular/angular-touch.min.js"/>"></script>

    <link rel="stylesheet" href="<c:url value="/lib/angular/angular-resizable.min.css"/>">
    <script src="<c:url value="/lib/angular/angular-resizable.min.js"/>"></script>

    <link rel="stylesheet" href="lib/markdown/editor.css" />
    <script type="text/javascript" src="lib/markdown/marked.js"></script>
    <script type="text/javascript" src="lib/markdown/editor.js"></script>

    <link rel="stylesheet" href="<c:url value="/res/admin.css"/>">
    <script src="<c:url value="/res/doyto.js"/>"></script>
    <script>
        var adminApp = angular.module('GRSApp', [
            'ngResource',
            'ngTouch',
            'angularResizable',
            'doyto.fzone',
            'ui.router',
            "ct.ui.router.extras.sticky"
        ]);
        var Config = window.Config || {};
        Config.models = ${menuList};
//        Config.models = [{"label":"用户管理面板","name":"dashboard","url":"#"},{"label":"菜单管理","name":"menu","url":"menu"},{"label":"权限管理","name":"perm","url":"perm"},{"label":"用户管理","name":"user","url":"user"},{"label":"角色管理","name":"role","url":"role"},{"label":"字典管理","name":"dict","url":"dict"},{"label":"分类管理","name":"category","url":"category"},{"label":"文章列表","name":"post","url":"post"}];
        Config.base = 'admin';
    </script>
    <script src="<c:url value="/res/admin.js"/>"></script>
</head>
<body class="dy-layout" ng-init="loadLoginUser()">
<header>
    <nav class="navbar navbar-inverse">
        <div class="container-fluid">
            <div class="navbar-header">
                <a class="navbar-brand" href="<c:url value="/"/>">
                    <small>
                        <i class="fa fa-leaf"></i>
                        APP后台管理系统
                    </small>
                </a>
            </div>
            <ul class="nav navbar-nav navbar-right">
                <%--<li><a href="#">Link</a></li>--%>
                <li class="dropdown">
                    <a href="#" class="dropdown-toggle" data-toggle="dropdown" role="button" aria-haspopup="true" aria-expanded="false">{{loginUser.nickname}}<span class="caret"></span></a>
                    <ul class="dropdown-menu">
                        <%--<li><a role=button><i class="fa fa-cog"></i>设置</a></li>--%>
                        <%--<li><a role=button><i class="fa fa-user"></i>个人资料 </a></li>--%>
                        <%--<li class="divider"></li>--%>
                        <li><a dy-href="logout"><i class="fa power-off"></i>退出</a></li>
                    </ul>
                </li>
            </ul>
        </div>
    </nav>
</header>
<section class="container-fluid row">
    <aside resizable r-directions="['right']">
        <div id="asideMenu" class="panel-group">
            <div class="panel panel-primary">
                <a class="panel-heading" role="tab" style="display:block">
                    <h4 class="panel-title">DASHBOARD</h4>
                </a>
            </div>
            <div class="panel panel-info" ng-repeat="menu in menuTree.submenu">
                <a class="panel-heading" role="tab" id="heading-{{menu.id}}" style="display:block"
                   data-toggle="collapse" data-parent="#asideMenu" data-target="#collapse-{{menu.id}}" aria-expanded="true" aria-controls="collapse-{{menu.id}}">
                    <h4 class="panel-title">{{menu.label}}</h4>
                </a>
                <div id="collapse-{{menu.id}}" class="panel-collapse collapse {{['in'][$index]}}" role="tabpanel" aria-labelledby="heading-{{menu.id}}">
                    <div class="list-group">
                        <a ui-sref="admin.{{sub.url}}" class="list-group-item" ng-repeat="sub in menu.submenu">{{sub.label}}</a>
                    </div>
                </div>
            </div>
        </div>
    </aside>
    <main ui-view class="content container" role="main"></main>
</section>
</body>
</html>
