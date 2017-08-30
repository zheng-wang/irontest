'use strict';

angular.module('irontest').factory('UDPs', ['$resource',
  function($resource) {
    return $resource('api/testcases/:testcaseId/udps', {
    }, {
      update: {
        method: 'PUT',
        url: 'api/udps/:udpId',
        params: { udpId: '@id' }
      }
    });
  }
]);
