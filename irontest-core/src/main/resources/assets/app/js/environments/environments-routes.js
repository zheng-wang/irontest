'use strict';

angular.module('irontest').config(['$stateProvider', function ($stateProvider) {
    $stateProvider
        .state('environment_all', {
            url: '/environments',
            templateUrl: '/ui/views/environments/list.html'
        })
        .state('environment_edit', {
            url: '/environments/:environmentId/edit',
            params: {
              newlyCreated: null
            },
            templateUrl: '/ui/views/environments/edit.html'
        });
}]);