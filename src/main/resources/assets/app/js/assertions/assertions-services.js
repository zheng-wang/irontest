'use strict';

//Teststeps service used for teststeps REST endpoint
angular.module('iron-test').factory('Assertions', ['$resource',
  function($resource) {
    return $resource('api/testcases/:testcaseId/teststeps/:teststepId/assertions/:assertionId', {
      testcaseId: '@testcaseId', teststepId: '@teststepId', assertionId: '@id'
    }, {
      update: {
        method: 'PUT'
      }
    });
  }
]);
