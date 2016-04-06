'use strict';

angular.module('iron-test').config(['$stateProvider', function ($stateProvider) {
    $stateProvider
        .state('endpoint_all', {
            url: '/endpoints',
            templateUrl: '/ui/views/endpoints/list.html'
        })
        .state('endpoint_create', {
            url: '/endpoints/create',
            templateUrl: '/ui/views/endpoints/edit.html'
        })
        .state('endpoint_edit', {
            url: '/endpoints/:endpointId/edit',
            templateUrl: '/ui/views/endpoints/edit.html'
        });
}]);