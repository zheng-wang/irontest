'use strict';

//  This controller is for managed endpoints.
angular.module('irontest').controller('UserLoginController', ['$scope', '$uibModal',
  function($scope, $uibModal) {
    $scope.openLoginModal = function() {
      //  open modal dialog
      var modalInstance = $uibModal.open({
        templateUrl: '/ui/views/userlogin/user-login-modal.html',
        controller: 'UserLoginModalController',
        windowClass: 'user-login-modal'
      });

      //  handle result from modal dialog
      modalInstance.result.then(function closed() {
        //  Do nothing.
      }, function dismissed() {
        //  Do nothing.
      });
    };
  }
]);
