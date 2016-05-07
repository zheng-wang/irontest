'use strict';

angular.module('iron-test').config(['$stateProvider', function ($stateProvider) {
    $stateProvider
        .state('testcase_all', {
            url: '/testcases',
            templateUrl: '/ui/views/testcases/list.html'
        })
        .state('testcase_edit', {
            url: '/testcases/:testcaseId/edit',
            params: {
              newlyCreated: null
            },
            templateUrl: '/ui/views/testcases/edit.html'
        });

}]);