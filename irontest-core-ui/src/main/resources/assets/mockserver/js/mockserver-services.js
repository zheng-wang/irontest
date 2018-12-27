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
      findAllUnmatchedStubRequests: {
        method: 'GET',
        url: '../api/mockserver/unmatchedStubRequests',
        isArray: true
      },
      findStubInstanceById: {
        method: 'GET',
        url: '../api/mockserver/stubInstances/:stubInstanceId'
      },
      findMatchedRequestsForStubInstance: {
        method: 'GET',
        url: '../api/mockserver/stubInstances/:stubInstanceId/stubRequests',
        isArray: true
      },
      findStubRequestById: {
        method: 'GET',
        url: '../api/mockserver/stubRequests/:stubRequestId'
      },
      clearRequestLog: {
        method: 'POST',
        url: '../api/mockserver/clearRequestLog'
      }
    });
  }
]);
