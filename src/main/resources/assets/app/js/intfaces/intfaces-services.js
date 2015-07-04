'use strict';

//Intfaces service used for intfaces REST intface
angular.module('service-testing-tool').factory('Intfaces', ['$resource',
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
