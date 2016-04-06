'use strict';

//Intfaces service used for intfaces REST intface
angular.module('iron-test').factory('Intfaces', ['$resource',
  function($resource) {
    return $resource('api/intfaces/:intfaceId', {
      intfaceId: '@id'
    }, {
      update: {
        method: 'PUT'
      }
    });
  }
]);
