'use strict';

//  This controller is for managed endpoints.
angular.module('irontest').controller('FoldersController', ['$scope', 'Folders', '$stateParams', '$state',
    '$timeout', 'IronTestUtils',
  function($scope, Folders, $stateParams, $state, $timeout, IronTestUtils) {
    var timer;
    $scope.autoSave = function(isValid) {
      if (timer) $timeout.cancel(timer);
      timer = $timeout(function() {
        $scope.update(isValid);
      }, 2000);
    };

    $scope.update = function(isValid) {
      if (isValid) {
        $scope.folder.$update(function(response) {
          $scope.$broadcast('successfullySaved');
          $scope.folder = response;
        }, function(response) {
          IronTestUtils.openErrorHTTPResponseModal(response);
        });
      } else {
        $scope.submitted = true;
      }
    };

    $scope.findOne = function() {
      Folders.get({
        folderId: $stateParams.folderId,
      }, function(folder) {
        $scope.folder = folder;
      }, function(response) {
        IronTestUtils.openErrorHTTPResponseModal(response);
      });
    };
  }
]);
