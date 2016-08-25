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