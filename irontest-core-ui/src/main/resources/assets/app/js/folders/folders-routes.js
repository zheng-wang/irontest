'use strict';

angular.module('irontest').config(['$stateProvider', function ($stateProvider) {
    $stateProvider
      .state('folder', {
          url: '/folder',
          templateUrl: '/ui/views/blank.html'
      });
}]);