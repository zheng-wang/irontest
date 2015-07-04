'use strict';

angular.module('service-testing-tool').config(['$stateProvider', function ($stateProvider) {
    $stateProvider
        .state('intface_all', {
            url: '/intfaces',
            templateUrl: '/ui/views/intfaces/list.html'
        })
        .state('intface_create', {
            url: '/intfaces/create',
            templateUrl: '/ui/views/intfaces/edit.html'
        })
        .state('intface_edit', {
            url: '/intfaces/:intfaceId/edit',
            templateUrl: '/ui/views/intfaces/edit.html'
        });
}]);