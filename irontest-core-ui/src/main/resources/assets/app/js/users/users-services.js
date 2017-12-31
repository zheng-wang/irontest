'use strict';

angular.module('irontest').factory('Users', ['$resource',
  function($resource) {
    return $resource('api/users/:userId', {
      userId: '@id'
    }, {
      updatePassword: {
        method: 'PUT',
        url: 'api/users/:userId/password'
      }
    });
  }
]);
