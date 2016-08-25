"use strict";

/**
 * 临时解决方案都写在这里，需要及时清理
 *
 * @author yuanzhen
 * @date 2016-03-26
 */

/* global adminApp*/
// 修正bootstrap的modal显示时，浏览器历史记录的前进或后退导致背景不消失的问题
adminApp.
run(["$rootScope", function ($rootScope) {
    $rootScope.$on('$stateChangeSuccess', function () {
        angular.forEach(document.querySelectorAll('.modal-backdrop'), function (elem) {
            elem.parentNode.removeChild(elem);
        });
    });
}]);

'use strict';

/* global angular, adminApp */

adminApp.
// config(['$locationProvider', function ($locationProvider) {
//     $locationProvider.html5Mode(true);
// }]).
// config(['$stickyStateProvider', function ($stickyStateProvider) {
//     $stickyStateProvider.enableDebug(true);
// }]).
config(['$stateProvider', '$urlRouterProvider', function ($stateProvider, $urlRouterProvider) {

    var config = angular.extend({
        base: 'app',
        models: []
    }, window.Config);

    angular.forEach(config.models, function(model) {
        var url = model.url;
        var uri = model.uri = model.url.split('?')[0];
        var state = {
            name: config.base + '.' + uri,
            url:  url,
            views: {}
        };
        state.views[uri] = {
            "templateUrl": "res/html/" + uri +".html"
        };
        if (model.sticky) {
            state.sticky = true;
            state.deepStateRedirect = true;
        }
        $stateProvider.state(state);
    });

    $stateProvider.
    state(config.base, {
        url: "/",
        abstract: true,
        template: function () {
            var html = '';
            angular.forEach(config.models, function (model) {
                html += '<div ui-view="' + model.uri +'" ng-show="$state.includes(\'' + config.base + '.' + model.uri +'\')"></div>';
            });
            return html;
        }
    });
    // For any unmatched url, redirect to /app/main
    $urlRouterProvider.otherwise("/");

}]).
config(['$resourceProvider', function ($resourceProvider) {
    $resourceProvider.defaults.actions.query.isArray = false;
}]).
provider('$menuStates', ['$stateProvider', function ($stateProvider) {
    // runtime dependencies for the service can be injected here, at the provider.$get() function.
    this.$get = function () { // for example
        return {
            add: function (name, state) {
                if (!state && name && name.name) {
                    state = name;
                    name = name.name;
                }
                try {
                    $stateProvider.state(name, state);
                } catch (e) {
                    console.log(e);
                }
            }
        };
    };
}]);


'use strict';

/* global adminApp */

adminApp.
run(['$rootScope', '$http',
    function ($rootScope, $http) {
        $rootScope.loadMenu = function () {
            $http.get('api/menu/tree').success(function (data) {
                $rootScope.menuTree = data.result;
            });
        };
        $rootScope.loadLoginUser = function () {
            $http.get('login-user').success(function (data) {
                if (data.success) {
                    $rootScope.loginUser = data.result;
                    $rootScope.loadMenu();
                } else {
                    location.href = 'login?redirect=' + encodeURIComponent(location.href);
                }
            });
        };
    }]
).
run(["$rootScope", "$state", function ($rootScope, $state) {
    $rootScope.$state = $state;
}]);
'use strict';

/*global adminApp, Crud, Util*/

adminApp.factory('Dict', ['$resource',
    function ($resource) {
        return $resource('api/dict/:id', {id: '@id'});
    }]
).
controller('DictCtrl', ['$scope', 'Dict',
    function ($scope, Dict) {
        $scope.crud = new Crud(Dict, function (data) {
            if (data.success) {
                $scope.crud.p.load();
                $('.modal').modal('hide');
            } else {
                Util.handleFailure(data);
            }
        });
    }]
);
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
'use strict';

/*global adminApp, Crud*/

adminApp.factory('Perm', ['$resource',
    function ($resource) {
        return $resource('api/perm/:id', {id: '@id'});
    }]
).
controller('PermCtrl', ['$scope', 'Perm',
    function ($scope, Perm) {
        $scope.crud = new Crud(Perm, function (data) {
            if (data.success) {
                $scope.crud.p.load();
                $('.modal').modal('hide');
            } else {
                Crud.handleFailure(data);
            }
        });
    }]
);
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