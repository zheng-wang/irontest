'use strict';

//  This controller is for managed endpoints.
angular.module('irontest').controller('UserLoginController', ['$scope', '$uibModal',
  function($scope, $uibModal) {
    $scope.openLoginModal = function() {
      $uibModal.open({
        templateUrl: '/ui/views/userlogin/user-login-modal.html',
        controller: 'UserLoginModalController',
        windowClass: 'user-login-modal'
      });
    };
  }
]);