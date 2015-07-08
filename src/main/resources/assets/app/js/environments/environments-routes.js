'use strict';

angular.module('service-testing-tool').config(['$stateProvider', function ($stateProvider) {
    $stateProvider
        .state('environment_all', {
            url: '/environments',
            templateUrl: '/ui/views/environments/list.html'
        })
        .state('environment_create', {
            url: '/environments/create',
            templateUrl: '/ui/views/environments/edit.html'
        })
        .state('environment_edit', {
            url: '/environments/:environmentId/edit',
            templateUrl: '/ui/views/environments/edit.html'
        })
        .state('enventry_create', {
            url: '/enventries/:environmentId/create',
            templateUrl: '/ui/views/environments/entryedit.html'
        })
        .state('enventry_edit', {
            url: '/enventries/:enventryId/edit',
            templateUrl: '/ui/views/environments/entryedit.html'
        });
}]);