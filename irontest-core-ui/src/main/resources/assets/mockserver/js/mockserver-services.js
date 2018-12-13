'use strict';

angular.module('mockserver').factory('MockServer', ['$resource',
  function($resource) {
    return $resource('../api/mockserver', {
    }, {
      findAllStubInstances: {
        method: 'GET',
        url: '../api/mockserver/stubInstances',
        isArray: true
      },
      findStubInstanceById: {
        method: 'GET',
        url: '../api/mockserver/stubInstances/:stubInstanceId'
      },
      findRequestsForStubInstance: {
        method: 'GET',
        url: '../api/mockserver/stubInstances/:stubInstanceId/stubRequests',
        isArray: true
      },
      findStubRequestById: {
        method: 'GET',
        url: '../api/mockserver/stubRequests/:stubRequestId'
      }
    });
  }
]);
