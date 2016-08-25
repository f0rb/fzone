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

