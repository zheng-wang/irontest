'use strict';

angular.module('irontest').factory('Users', ['$resource',
  function($resource) {
    return $resource('api/users/:userId', {
      userId: '@id'
    }, {
      update: {
        method: 'PUT'
      }
    });
  }
]);
