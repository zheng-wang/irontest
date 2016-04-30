'use strict';

//Endpoints service used for endpoints REST endpoint
angular.module('iron-test').factory('Endpoints', ['$resource',
  function($resource) {
    return $resource('api/environments/:environmentId/endpoints/:endpointId', {
      environmentId: '@environment.id', endpointId: '@id'
    }, {
      update: {
        method: 'PUT'
      }
    });
  }
]);
