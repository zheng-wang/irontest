'use strict';

angular.module('iron-test').controller('TeststepsController', ['$scope', 'Teststeps', '$stateParams', '$timeout',
    '$uibModal', 'IronTestUtils',
  function($scope, Teststeps, $stateParams, $timeout, $uibModal, IronTestUtils) {
    $scope.teststep = {};

    var timer;
    //  use object instead of primitives, so that child scope can update the values
    $scope.savingStatus = {
      saveSuccessful: null,
      savingErrorMessage: null
    };

    $scope.autoSave = function(isValid) {
      if (timer) $timeout.cancel(timer);
      timer = $timeout(function() {
        $scope.update(isValid);
      }, 2000);
    };

    $scope.update = function(isValid) {
      if (isValid) {
        $scope.teststep.$update(function(response) {
          $scope.savingStatus.saveSuccessful = true;
          $scope.teststep = response;
        }, function(error) {
          $scope.savingStatus.savingErrorMessage = error.data.message;
          $scope.savingStatus.saveSuccessful = false;
        });
      } else {
        $scope.savingStatus.submitted = true;
      }
    };

    $scope.findOne = function() {
      Teststeps.get({
        testcaseId: $stateParams.testcaseId,
        teststepId: $stateParams.teststepId
      }, function (response) {
        $scope.teststep = response;
      }, function(response) {
        IronTestUtils.openErrorMessageModal(response);
      });
    };

    $scope.selectManagedEndpoint = function(endpointType) {
      var modalInstance = $uibModal.open({
        templateUrl: '/ui/views/endpoints/list-modal.html',
        controller: 'EndpointsModalController',
        size: 'lg',
        resolve: {
          endpointType: function () {
            return endpointType;
          }
        }
      });

      modalInstance.result.then(function (selectedEndpoint) {
        $scope.teststep.endpoint = selectedEndpoint;
        $scope.autoSave(true);
      }, function () {
        //  Modal dismissed
      });
    };
  }
]);
