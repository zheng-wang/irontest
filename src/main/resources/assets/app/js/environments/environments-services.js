'use strict';

//Environments service used for environments REST endpoint
angular.module('iron-test').factory('Environments', ['$resource',
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
