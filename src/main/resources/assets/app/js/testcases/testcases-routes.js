'use strict';

angular.module('service-testing-tool').config(['$stateProvider', function ($stateProvider) {
    $stateProvider
        .state('testcase_all', {
            url: '/testcases',
            templateUrl: '/ui/views/testcases/list.html'
        })
        .state('testcase_create', {
            url: '/testcases/create',
            templateUrl: '/ui/views/testcases/create.html'
        })
        .state('testcase_edit', {
            url: '/testcases/:testcaseId/edit',
            templateUrl: '/ui/views/testcases/edit.html'
        });

}]);