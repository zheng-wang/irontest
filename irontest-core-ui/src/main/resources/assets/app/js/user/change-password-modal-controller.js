'use strict';

angular.module('irontest').controller('ChangePasswordModalController', ['$scope', '$uibModalInstance',
  function($scope, $uibModalInstance) {
    $scope.ok = function() {
      $uibModalInstance.close($scope.newPassword);
    };

    $scope.cancel = function () {
      $uibModalInstance.dismiss('cancel');
    };
  }
]);