'use strict';

//Teststeps service used for teststeps REST endpoint
angular.module('service-testing-tool').factory('Teststeps', ['$resource',
  function($resource) {
    return $resource('api/testcases/:testcaseId/teststeps/:teststepId', {
      testcaseId: '@testcaseId', teststepId: '@id'
    }, {
      update: {
        method: 'PUT'
      }
    });
  }
]);
