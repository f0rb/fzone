'use strict';

/*global adminApp, Crud, Util*/

adminApp.
factory('Menu', ['$resource',
    function ($resource) {
        return $resource('api/menu/:id', {id: '@id'}, {
            query: {isArray: false},
            parent: {
                url: 'api/menu/parent',
                method: 'GET'
            }
        });
    }]
).
controller('MenuCtrl', ['$scope', 'Menu',
    function ($scope, Menu) {
        var defaultErrorMessage = '访问错误';

        Menu.parent({}, function (data) {
            if (data.success) {
                $scope.parents = data.result;
            } else {
                alert(data.message || defaultErrorMessage);
            }
        });
        Menu.query({}, function (data) {
            if (data.success) {
                $scope.menus = data.result;
            } else {
                alert(data.message || defaultErrorMessage);
            }
        });

        /*
        function simpleCopy(source) {
            var destination = {}, exclude = 'createTime|createUserId|updateTime|updateUserId|deleted';
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
                $scope.loadMenu();//更新左侧的菜单树
                $('.modal').modal('hide');
            } else {
                alert(data.message || defaultErrorMessage);
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
            $scope.menu = {};
        };

        $scope.edit = function (menu) {
            $scope.menu = simpleCopy(menu);
        };

        $scope.view = function (menu) {
            $scope.menu = menu;
        };

        $scope.save = function (menu) {
            Menu.save(menu, onSuccess, onError);
        };

        $scope.remove = function (menu) {
            if (!confirm("确定要删除菜单[" + menu.name + "]吗?")) {
                return;
            }
            Menu.remove({}, {id: menu.id}, onSuccess, onError);
        };

        $scope.p = new Page(Menu.query).load();
        */

        $scope.crud = new Crud(Menu, function (data) {
            if (data.success) {
                $scope.crud.p.load();
                $scope.loadMenu();//更新左侧的菜单树
                $('.modal').modal('hide');
            } else {
                Util.handleFailure(data);
            }
        });
    }]
);