'use strict';

angular.module('irontest').factory('PropertyExtractors', ['$resource',
  function($resource) {
    return $resource('api/teststeps/:teststepId/propertyExtractors', {
    }, {
      update: {
        method: 'PUT',
        url: 'api/propertyExtractors/:propertyExtractorId',
        params: { propertyExtractorId: '@id' }
      },
      remove: {
        method: 'DELETE',
        url: 'api/propertyExtractors/:propertyExtractorId',
        params: { propertyExtractorId: '@id' }
      },
      extract: {
        method: 'POST',
        url: 'api/propertyExtractors/:propertyExtractorId/extract'
      }
    });
  }
]);
