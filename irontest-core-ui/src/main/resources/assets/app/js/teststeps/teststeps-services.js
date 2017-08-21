'use strict';

angular.module('irontest').factory('Teststeps', ['$resource',
  function($resource) {
    return $resource('api/testcases/:testcaseId/teststeps/:teststepId/:verb', {
      testcaseId: '@testcaseId', teststepId: '@id'
    }, {
      update: {
        method: 'PUT'
      },
      run: {
        method: 'POST',
        params: {
          verb: 'run'
        }
      }
    });
  }
]);
