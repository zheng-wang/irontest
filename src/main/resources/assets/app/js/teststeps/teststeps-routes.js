'use strict';

angular.module('service-testing-tool').config(['$stateProvider', function ($stateProvider) {
    $stateProvider
        .state('teststep_all', {
            url: '/teststeps',
            templateUrl: '/ui/views/teststeps/list.html'
        })
        .state('teststep_create', {
            url: '/teststeps/create',
            templateUrl: '/ui/views/teststeps/create.html'
        })
        .state('teststep_edit', {
            url: '/teststeps/:teststepId/edit',
            templateUrl: '/ui/views/teststeps/edit.html'
        });

}]);