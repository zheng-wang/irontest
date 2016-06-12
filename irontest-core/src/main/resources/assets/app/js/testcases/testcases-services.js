'use strict';

//Testcases service used for testcases REST endpoint
angular.module('irontest').factory('Testcases', ['$resource',
  function($resource) {
    return $resource('api/testcases/:testcaseId', {
      testcaseId: '@id'
    }, {
      update: {
        method: 'PUT'
      }
    });
  }
]);
