'use strict';

//  NOTICE:
//    The $scope here prototypically inherits from the $scope of TeststepsController.
//    ng-include also creates a scope.
angular.module('irontest').controller('TeststepsActionController', ['$scope',
  function($scope) {
    $scope.showAssertionsArea = false;
    $scope.showPropertyExtractorsArea = false;

    var removeBottomPaneFromColumn = function() {
      var elementHeight = document.getElementById('bottomPane').offsetHeight;
      $scope.$broadcast('elementRemovedFromColumn', { elementHeight: elementHeight });
    };

    $scope.toggleAssertionsArea = function() {
      if ($scope.showAssertionsArea) {    //  for toggle off
        removeBottomPaneFromColumn();
      }
      $scope.showAssertionsArea = !$scope.showAssertionsArea;
    };

    $scope.togglePropertyExtractorsArea = function() {
      if ($scope.showPropertyExtractorsArea) {    //  for toggle off
        removeBottomPaneFromColumn();
      }
      $scope.showPropertyExtractorsArea = !$scope.showPropertyExtractorsArea;
    };

    $scope.bottomPaneLoadedCallback = function() {
      var elementHeight = document.getElementById('bottomPane').offsetHeight;
      $scope.$broadcast('elementInsertedIntoColumn', { elementHeight: elementHeight });
    };
  }
]);
