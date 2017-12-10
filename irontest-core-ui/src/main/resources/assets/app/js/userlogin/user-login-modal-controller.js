'use strict';

angular.module('irontest').controller('UserLoginModalController', ['$scope', '$uibModalInstance', '$window', '$http',
  function($scope, $uibModalInstance, $window, $http) {
    $scope.authenticationFailed = null;
    $scope.login = function() {
      var auth = $window.btoa($scope.username + ':' + $scope.password);
      $http
        .get('api/authenticated', {headers: {'Authorization': 'Basic ' + auth}})
        .then(function successCallback(response) {
          $scope.authenticationFailed = false;
        }, function errorCallback(response) {
          $scope.authenticationFailed = true;
        });
    };

    $scope.cancel = function () {
      $uibModalInstance.dismiss('cancel');
    };
  }
]);