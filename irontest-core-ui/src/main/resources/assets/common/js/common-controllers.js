'use strict';

angular.module('common').controller('ErrorMessageModalController', ['$scope', '$uibModalInstance',
    'errorMessage', 'errorDetails',
  function($scope, $uibModalInstance, errorMessage, errorDetails) {
    $scope.errorMessage = errorMessage;
    $scope.errorDetails = errorDetails;

    $scope.close = function () {
      $uibModalInstance.close();
    };
  }
]);
