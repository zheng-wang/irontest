'use strict';

//Teststeps service used for teststeps REST endpoint
angular.module('irontest').factory('Teststeps', ['$resource',
  function($resource) {
    return $resource('api/testcases/:testcaseId/teststeps/:teststepId/:verb', {
      testcaseId: '@testcaseId', teststepId: '@id', verb: null
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
