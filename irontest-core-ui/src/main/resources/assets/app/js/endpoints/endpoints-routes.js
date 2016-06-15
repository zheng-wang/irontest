'use strict';

angular.module('irontest').config(['$stateProvider', function ($stateProvider) {
  $stateProvider
    .state('endpoint_edit', {
        url: '/environments/:environmentId/endpoints/:endpointId/edit',
        params: {
          newlyCreated: null
        },
        templateUrl: '/ui/views/endpoints/edit.html'
    });
}]);