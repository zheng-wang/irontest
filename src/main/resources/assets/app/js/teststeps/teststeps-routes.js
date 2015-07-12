'use strict';

angular.module('service-testing-tool').config(['$stateProvider', function ($stateProvider) {
    $stateProvider
        .state('teststep_create', {
            url: '/testcases/:testcaseId/teststeps/create',
            templateUrl: '/ui/views/teststeps/create.html'
        })
        .state('teststep_edit', {
            url: '/testcases/:testcaseId/teststeps/:teststepId/edit',
            templateUrl: '/ui/views/teststeps/edit.html'
        });

}]);