'use strict';

angular.module('irontest').config(['$stateProvider', function ($stateProvider) {
    $stateProvider
      .state('teststep_edit', {
        url: '/testcases/:testcaseId/teststeps/:teststepId/edit',
        params: {
          newlyCreated: null
        },
        templateUrl: '/ui/views/teststeps/edit.html'
      });

}]);