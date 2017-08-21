'use strict';

angular.module('irontest').factory('Environments', ['$resource',
  function($resource) {
    return $resource('api/environments/:environmentId', {
      environmentId: '@id'
    }, {
      update: {
        method: 'PUT'
      }
    });
  }
]);
