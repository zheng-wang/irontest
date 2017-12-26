'use strict';

//  This controller is for managed endpoints.
angular.module('irontest').controller('AuthenticationController', ['$scope', '$rootScope', '$uibModal', '$window',
    '$http',
  function($scope, $rootScope, $uibModal, $window, $http) {
    $scope.openLoginModal = function() {
      $uibModal.open({
        templateUrl: '/ui/views/authentication/login-modal.html',
        controller: 'UserLoginModalController',
        size: 'md',
        windowClass: 'login-modal'
      });
    };

    $scope.logout = function() {
      $rootScope.appStatus.userInfo = null;
      $window.localStorage.removeItem("userInfo");
      delete $http.defaults.headers.common.Authorization;

      $scope.$emit('userLoggedOut');
    };
  }
]);