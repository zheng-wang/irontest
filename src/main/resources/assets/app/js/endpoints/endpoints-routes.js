'use strict';

angular.module('iron-test').config(['$stateProvider', function ($stateProvider) {
    $stateProvider
        .state('endpoint_create_soap', {
            url: '/environments/:environmentId/endpoints/createSOAPEndpoint',
            templateUrl: '/ui/views/endpoints/soap/create-soap-endpoint.html'
        })
        .state('endpoint_edit', {
            url: '/environments/:environmentId/endpoints/:endpointId/edit',
            templateUrl: '/ui/views/endpoints/edit.html'
        });
}]);