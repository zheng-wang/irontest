'use strict';

angular.module('mockserver').config(['$stateProvider', function ($stateProvider) {
  $stateProvider
    .state('stub_instance', {
        url: '/stubInstances/:stubInstanceId',
        templateUrl: '/ui/mockserver/views/stub-instance-show.html'
    })
    .state('stub_request', {
        url: '/stubRequests/:stubRequestId',
        templateUrl: '/ui/mockserver/views/stub-request-show.html'
    });
}]);