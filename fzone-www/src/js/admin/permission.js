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