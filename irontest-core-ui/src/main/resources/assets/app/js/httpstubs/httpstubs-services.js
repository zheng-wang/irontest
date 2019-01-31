'use strict';

angular.module('irontest').factory('HTTPStubs', ['$resource',
  function($resource) {
    return $resource('api/testcases/:testcaseId/httpstubs/:httpStubId', {
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
      },
      loadAll: {
        method: 'POST',
        url: 'api/testcases/:testcaseId/httpstubs/loadAll',
      },
      move: {
        method: 'POST',
        url: 'api/testcases/:testcaseId/httpstubs/move',
        isArray: true
      }
    });
  }
]);
