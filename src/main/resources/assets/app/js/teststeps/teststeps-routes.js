'use strict';

angular.module('iron-test').config(['$stateProvider', function ($stateProvider) {
    $stateProvider
        .state('teststep_create_db', {
            url: '/testcases/:testcaseId/teststeps/createDBTestStep',
            templateUrl: '/ui/views/teststeps/db/create-db-teststep.html'
        })
        .state('teststep_edit', {
            url: '/testcases/:testcaseId/teststeps/:teststepId/edit',
            params: {
              newlyCreated: null
            },
            templateUrl: '/ui/views/teststeps/edit.html'
        });

}]);