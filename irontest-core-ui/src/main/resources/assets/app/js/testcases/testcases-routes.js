'use strict';

angular.module('irontest').config(['$stateProvider', function ($stateProvider) {
  $stateProvider
    .state('testcase_edit', {
      url: '/testcases/:testcaseId/edit',
      templateUrl: '/ui/views/testcases/edit.html'
    });
}]);