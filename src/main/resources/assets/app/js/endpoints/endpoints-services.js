'use strict';

//Endpoints service used for endpoints REST endpoint
angular.module('service-testing-tool').factory('Endpoints', ['$resource',
  function($resource) {
    return $resource('api/endpoints/:endpointId', {
      endpointId: '@id'
    }, {
      update: {
        method: 'PUT'
      },
      getProperties: {
        method: 'GET',
        url: '/api/endpoints/handler/:handlerName',
        params: {handlerName: '@name'},
        isArray: true
      }
    });
  }
]);
