'use strict';

angular.module('irontest').config(['$stateProvider', function ($stateProvider) {
    $stateProvider
        .state('user_all', {
            url: '/users',
            templateUrl: '/ui/views/users/list.html'
        })
        .state('user_edit', {
            url: '/users/:userId/edit',
            params: {
              newlyCreated: null
            },
            templateUrl: '/ui/views/users/edit.html'
        });
}]);