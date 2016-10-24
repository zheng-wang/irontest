'use strict';

//Testcases service used for testcases REST endpoint
angular.module('irontest').factory('Testcases', ['$resource',
  function($resource) {
    return $resource('api/testcases/:testcaseId/:verb', {
      testcaseId: '@id'
    }, {
      update: {
        method: 'PUT'
      },
      duplicate: {
        method: 'PUT',
        params: {
          verb: 'duplicate'
        }
      }
    });
  }
]);
