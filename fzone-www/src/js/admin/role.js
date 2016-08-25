'use strict';

/*global adminApp, Util, Crud*/

adminApp.factory('Role', ['$resource',
    function ($resource) {
        return $resource('api/role/:id', {id: '@id'},
            {
                updateMenu: {url: 'api/role/:id/menu', method: 'POST'}
            }
        );
    }]
).
controller('RoleCtrl', ['$scope', 'Role',
    function ($scope, Role) {
        $scope.crud = new Crud(Role, function (data) {
            if (data.success) {
                $scope.crud.p.load();
                $('.modal').modal('hide');
            } else {
                Util.handleFailure(data);
            }
        });

        //获取角色已配置的菜单
        $scope.crud.getMenu = function (role) {
            $scope.crud.record = role;
            $scope.roleMenus = {};
            Role.get({id:role.id}, function (data) {
                if (data.success) {
                    $scope.crud.record = data.result;
                    var menus = data.result.menus;
                    if (menus) {
                        for (var i = 0; i < menus.length; i++) {
                            $scope.roleMenus[menus[i]] = true;
                        }
                    }
                }
            });
        };

        //更新角色菜单
        $scope.crud.configMenu = function (role) {
            var roleMenus = $scope.roleMenus, menus = [];
            for (var key in roleMenus) {
                if (roleMenus[key]) {
                    menus.push(Number(key));
                }
            }
            Role.updateMenu({id: role.id}, menus,
                function (data) {
                    if (data.success) {
                        $('.modal').modal('hide');
                    } else {
                        alert(data.message || "访问失败");
                    }
                },
                function (data) {
                    Util.handleFailure(data);
                }
            );
        };
    }]
);