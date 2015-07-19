'use strict';

//Teststeps service used for teststeps REST endpoint
angular.module('service-testing-tool').factory('Assertions', ['$resource',
  function($resource) {
    return $resource('api/testcases/:testcaseId/teststeps/:teststepId/assertions', {
      testcaseId: '@testcaseId', teststepId: '@teststepId'
    }, {
      update: {
        method: 'PUT'
      }
    });
  }
]);
