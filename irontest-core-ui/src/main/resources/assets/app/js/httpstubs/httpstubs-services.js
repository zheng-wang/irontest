'use strict';

angular.module('irontest').factory('HTTPStubs', ['$resource',
  function($resource) {
    return $resource('api/testcases/:testcaseId/httpstubs', {
    }, {
      update: {
        method: 'PUT',
        url: 'api/httpstubs/:httpStubId',
        params: { httpStubId: '@id' }
      },
      remove: {
        method: 'DELETE',
        url: 'api/httpstubs/:httpStubId',
        params: { httpStubId: '@id' }
      }
    });
  }
]);
