'use strict';

//  This controller is for managed endpoints.
angular.module('irontest').controller('AuthenticationController', ['$scope', '$uibModal', '$window', '$http',
  function($scope, $uibModal, $window, $http) {
    $scope.openLoginModal = function() {
      $uibModal.open({
        templateUrl: '/ui/views/authentication/login-modal.html',
        controller: 'UserLoginModalController',
        size: 'md',
        windowClass: 'login-modal'
      });
    };

    $scope.logout = function() {
      $window.localStorage.removeItem("authHeaderValue");
      $window.localStorage.removeItem("username");
      delete $http.defaults.headers.common.Authorization;

      $scope.$emit('userLoggedOut');
    };
  }
]);