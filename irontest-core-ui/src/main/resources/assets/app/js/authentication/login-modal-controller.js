'use strict';

angular.module('irontest').controller('UserLoginModalController', ['$scope', '$uibModalInstance',
    '$window', '$http',
  function($scope, $uibModalInstance, $window, $http) {
    $scope.authenticationFailed = null;

    $scope.login = function() {
      var authHeaderValue = 'Basic ' + $window.btoa($scope.username + ':' + $scope.password);
      $http
        .get('api/authenticated', {headers: {'Authorization': authHeaderValue}})
        .then(function successCallback(response) {
          $window.localStorage.authHeaderValue = authHeaderValue;
          $window.localStorage.username = $scope.username;
          $http.defaults.headers.common.Authorization = authHeaderValue;

          $uibModalInstance.dismiss();
        }, function errorCallback(response) {
          $scope.authenticationFailed = true;
        });
    };

    $scope.cancel = function () {
      $uibModalInstance.dismiss('cancel');
    };
  }
]);