'use strict';

angular.module('irontest').controller('CreateUserModalController', ['$scope', '$uibModalInstance',
  function($scope, $uibModalInstance) {
    $scope.cancel = function () {
      $uibModalInstance.dismiss('cancel');
    };

    $scope.ok = function() {
      $uibModalInstance.close($scope.username);
    };
  }
]);
