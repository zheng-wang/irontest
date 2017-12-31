'use strict';

angular.module('irontest').controller('UserController', ['$scope', '$rootScope', '$uibModal', 'Users',
  function($scope, $rootScope, $uibModal, Users) {
    $scope.openLoginModal = function() {
      $uibModal.open({
        templateUrl: '/ui/views/user/login-modal.html',
        controller: 'UserLoginModalController',
        size: 'md',
        windowClass: 'login-modal'
      });
    };

    $scope.openChangePasswordModal = function() {
      //  open modal dialog
      var modalInstance = $uibModal.open({
        templateUrl: '/ui/views/user/change-password-modal.html',
        controller: 'ChangePasswordModalController',
        size: 'md',
        windowClass: 'change-password-modal',
      });

      //  handle result from modal dialog
      modalInstance.result.then(function closed(newPassword) {
        var user = new Users();

        //  save the new password
        user.$updatePassword({ userId: $rootScope.appStatus.getUserId(), newPassword: newPassword
        }, function successCallback(response) {
          $rootScope.logout();
        }, function errorCallback(response) {
          IronTestUtils.openErrorHTTPResponseModal(response);
        });
      }, function dismissed() {
        //  Modal dismissed. Do nothing.
      });
    };
  }
]);