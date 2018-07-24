'use strict';

angular.module('irontest').controller('ImportTestcaseModalController', ['$scope', '$uibModalInstance',
  function($scope, $uibModalInstance) {
    $scope.cancel = function () {
      $uibModalInstance.dismiss('cancel');
    };

    $scope.fileSelected = function(file) {
      $uibModalInstance.close(file);
    }
  }
]);
