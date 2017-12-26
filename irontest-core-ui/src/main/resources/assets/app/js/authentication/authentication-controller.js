'use strict';

angular.module('irontest').controller('AuthenticationController', ['$scope', '$uibModal',
  function($scope, $uibModal) {
    $scope.openLoginModal = function() {
      $uibModal.open({
        templateUrl: '/ui/views/authentication/login-modal.html',
        controller: 'UserLoginModalController',
        size: 'md',
        windowClass: 'login-modal'
      });
    };
  }
]);