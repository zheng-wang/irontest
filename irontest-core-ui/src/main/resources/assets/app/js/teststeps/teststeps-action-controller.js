'use strict';

//  NOTICE:
//    The $scope here prototypically inherits from the $scope of TeststepsController.
//    ng-include also creates a scope.
angular.module('irontest').controller('TeststepsActionController', ['$scope',
  function($scope) {
    $scope.showBottomPane = false;
    $scope.bottomButtonModel = { selectedButton: null};

    $scope.removeBottomPaneFromColumn = function() {
      var elementHeight = document.getElementById('bottomPane').offsetHeight;
      $scope.$broadcast('elementRemovedFromColumn', { elementHeight: elementHeight });
    };

    $scope.$watch('bottomButtonModel.selectedButton', function(newValue, oldValue) {
      if (oldValue && !newValue) {
        $scope.removeBottomPaneFromColumn();
        $scope.showBottomPane = false;
      } else if (oldValue && newValue) {
        $scope.removeBottomPaneFromColumn();
        $scope.showBottomPane = true;
      } else if (!oldValue && newValue) {
        $scope.showBottomPane = true;
      }
    });

    $scope.bottomPaneLoadedCallback = function() {
      var elementHeight = document.getElementById('bottomPane').offsetHeight;
      $scope.$broadcast('elementInsertedIntoColumn', { elementHeight: elementHeight });
    };
  }
]);
