'use strict';

angular.module('irontest').factory('ManagedEndpoints', ['$resource',
  function($resource) {
    return $resource('api/endpoints/managed/:endpointId', {
      endpointId: '@id'
    }, {
      update: {
        method: 'PUT'
      }
    });
  }
]);
