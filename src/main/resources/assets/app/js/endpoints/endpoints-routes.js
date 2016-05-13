'use strict';

angular.module('iron-test').config(['$stateProvider', function ($stateProvider) {
  $stateProvider
    .state('endpoint_create_db', {
        url: '/environments/:environmentId/endpoints/createDBEndpoint',
        templateUrl: '/ui/views/endpoints/db/create-db-endpoint.html'
    })
    .state('endpoint_edit', {
        url: '/environments/:environmentId/endpoints/:endpointId/edit',
        params: {
          newlyCreated: null
        },
        templateUrl: '/ui/views/endpoints/edit.html'
    });
}]);