'use strict';

//  NOTICE:
//    The $scope here prototypically inherits from the $scope of TeststepsController.
//    ng-include also creates a scope.
angular.module('irontest').controller('TeststepsActionController', ['$scope',
  function($scope) {
    $scope.showAssertionsArea = false;

    $scope.toggleAssertionsArea = function() {
      if ($scope.showAssertionsArea) {    //  for toggle off
        var elementHeight = document.getElementById('assertionsArea').offsetHeight;
        $scope.$broadcast('elementRemovedFromColumn', { elementHeight: elementHeight });
      }

      $scope.showAssertionsArea = !$scope.showAssertionsArea;
    };

    $scope.assertionsAreaLoadedCallback = function() {
      var elementHeight = document.getElementById('assertionsArea').offsetHeight;
      $scope.$broadcast('elementInsertedIntoColumn', { elementHeight: elementHeight });
    };
  }
]);
