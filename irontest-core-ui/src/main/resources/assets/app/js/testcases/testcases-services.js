'use strict';

angular.module('irontest').factory('Testcases', ['$resource',
  function($resource) {
    return $resource('api/testcases/:testcaseId/:verb', {
      testcaseId: '@id'
    }, {
      update: {
        method: 'PUT'
      },
      duplicate: {
        method: 'POST',
        params: {
          verb: 'duplicate'
        }
      },
      moveStep: {
        method: 'PATCH',
        params: {
          verb: 'moveStep'
        }
      }
    });
  }
]);
