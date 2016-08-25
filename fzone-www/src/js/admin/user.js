'use strict';

/*global adminApp, Util, Page*/

adminApp.factory('User', ['$resource',
    function ($resource) {
        return $resource('api/user/:id', {id: '@id'},
            {
                query: {isArray: false},
                updateRole: {url: 'api/user/:id/role', method: 'POST'},
                roles: {url: 'api/role/list'}
            }
        );
    }]
).controller('UserCtrl', ['$scope', 'User',
    function ($scope, User) {
        var defaultErrorMessage = '访问错误';

        function simpleCopy(source) {
            var destination = {}, exclude = "createTime|createUserId|updateTime|updateUserId|deleted";
            for (var key in source) {
                if (source.hasOwnProperty(key) && exclude.indexOf(key) < 0) {
                    destination[key] = source[key];
                }
            }
            return destination;
        }

        function onSuccess(data) {
            if (data.success) {
                $scope.p.load();
                $('.modal').modal('hide');
            } else {
                Util.handleFailure(data);
            }
        }

        function onError(ret) {
            if (typeof ret === 'object') {
                alert('Status[' + ret.status + ']: ' + (ret.data.message || ret.statusText || defaultErrorMessage));
            } else {
                alert(typeof ret === 'string' ? ret : defaultErrorMessage);
            }
        }

        $scope.add = function () {
            $scope.user = {};
            $scope.userRoles = {};
        };

        $scope.edit = function (user) {
            $scope.user = simpleCopy(user);
        };

        $scope.view = function (user) {
            $scope.user = user;
        };

        $scope.save = function (user) {
            user.roles = [];
            for (var key in $scope.userRoles) {
                if ($scope.userRoles[key]) {
                    user.roles.push(Number(key));
                }
            }
            User.save(user, onSuccess, onError);
        };

        User.roles(function (data) {
            $scope.roles = data.result;
        });

        $scope.updateRole = function (user) {
            var u = {id: user.id};
            u.roles = [];
            for (var key in $scope.userRoles) {
                if ($scope.userRoles[key]) {
                    u.roles.push(Number(key));
                }
            }
            User.updateRole(u, onSuccess, onError);
        };

        $scope.role = function (user) {
            $scope.user = user;
            $scope.userRoles = {};
            User.get({id:user.id}, function (data) {
                if (data.success) {
                    $scope.user = data.result;
                    if ($scope.user.roles) {
                        for (var i = 0; i < $scope.user.roles.length; i++) {
                            $scope.userRoles[$scope.user.roles[i].id] = true;
                        }
                    }
                }
            });
        };

        $scope.remove = function (user) {
            if (!confirm("确定要删除用户[" + user.username + "]吗?")) {
                return;
            }
            User.remove({}, {id: user.id}, onSuccess, onError);
        };

        $scope.p = new Page(User.query).load();
    }]
);