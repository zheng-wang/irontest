'use strict';

//  NOTICE:
//    The $scope here prototypically inherits from the $scope of the specific test step controller.
//    ng-include also creates a scope.
angular.module('irontest').controller('PropertyExtractorsController', ['$scope',
  function($scope) {
    $scope.bottomPaneLoadedCallback();
  }
]);
