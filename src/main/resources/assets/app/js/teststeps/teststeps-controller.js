'use strict';

angular.module('iron-test').controller('TeststepsController', ['$scope', 'Teststeps', '$stateParams', '$timeout',
    '$uibModal', 'IronTestUtils', '$http', 'Environments',
  function($scope, Teststeps, $stateParams, $timeout, $uibModal, IronTestUtils, $http, Environments) {
    $scope.teststep = {};

    var timer;
    //  use object instead of primitives, so that child scope can update the values
    $scope.savingStatus = {
      saveSuccessful: null,
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
        }, function(response) {
          IronTestUtils.openErrorMessageModal(response);
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
      //  find managed endpoints by type
      var url = 'api/jsonservice/findManagedEndpointsByType?type=' + endpointType;
      $http
        .get(url)
        .then(function successCallback(response) {
          //  open modal dialog
          var modalInstance = $uibModal.open({
            templateUrl: '/ui/views/endpoints/list-modal.html',
            controller: 'SelectManagedEndpointModalController',
            size: 'lg',
            windowClass: 'select-managed-endpoint-modal',
            resolve: {
              endpoints: function () {
                return response.data;
              }
            }
          });

          //  handle result from modal dialog
          modalInstance.result.then(function (selectedEndpoint) {
            $scope.teststep.endpoint = selectedEndpoint;
            $scope.update(true);  //  save immediately (no timeout)
          }, function () {
            //  Modal dismissed
          });
        }, function errorCallback(response) {
          IronTestUtils.openErrorMessageModal(response);
        });
    };

    $scope.shareEndpoint = function() {
      //  find all environments
      Environments.query(function(environments) {
        $scope.environments = environments;
      }, function(response) {
        IronTestUtils.openErrorMessageModal(response);
      });
    };
  }
]);
