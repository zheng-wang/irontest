'use strict';

angular.module('mockserver').config(['$stateProvider', function ($stateProvider) {
  $stateProvider
    .state('stub_instance', {
        url: '/stubInstances/:stubInstanceId',
        templateUrl: '/ui/mockserver/views/stub-instance-show.html'
    });
}]);