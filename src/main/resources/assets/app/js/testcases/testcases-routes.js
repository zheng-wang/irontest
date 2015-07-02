'use strict';

angular.module('service-testing-tool').config(['$stateProvider', function ($stateProvider) {
    $stateProvider
        .state('testcase_all', {
            url: '/testcases',
            templateUrl: '/ui/views/testcases/list.html'
        })
        .state('testcase_create', {
            url: '/testcases/create',
            templateUrl: '/ui/views/testcases/create_edit.html'
        });

}]);