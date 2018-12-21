'use strict';

angular.module('mockserver').config(['$stateProvider', function ($stateProvider) {
  $stateProvider
    .state('stub_instance', {
      url: '/stubInstances/:stubInstanceId',
      templateUrl: '/ui/mockserver/views/stub-instance-show.html'
    })
    .state('matched_stub_request', {
      url: '/stubInstances/:stubInstanceId/stubRequests/:stubRequestId',
      templateUrl: '/ui/mockserver/views/stub-request-show.html'
    })
    .state('unmatched_stub_request', {
      url: '/unmatchedStubRequests/:stubRequestId',
      templateUrl: '/ui/mockserver/views/stub-request-show.html'
    });
}]);