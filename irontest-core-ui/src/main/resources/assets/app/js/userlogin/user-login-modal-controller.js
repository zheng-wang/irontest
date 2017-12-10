'use strict';

angular.module('irontest').controller('UserLoginModalController', ['$scope', '$uibModalInstance', '$window', '$http',
  function($scope, $uibModalInstance, $window, $http) {
    $scope.login = function() {
      var auth = $window.btoa($scope.username + ':' + $scope.password);
      $http
        .get('api/authenticated', {headers: {'Authorization': 'Basic ' + auth}})
        .then(function successCallback(response) {
          console.log(response);
        }, function errorCallback(response) {
          console.error(response);
        });
    };

    $scope.cancel = function () {
      $uibModalInstance.dismiss('cancel');
    };
  }
]);