'use strict';

angular.module('mockserver').factory('MockServer', ['$resource',
  function($resource) {
    return $resource('../api/mockserver', {
    }, {
      findAllStubInstances: {
        method: 'GET',
        url: '../api/mockserver/stubInstances',
        isArray: true
      }
    });
  }
]);
