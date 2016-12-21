'use strict';

angular.module('irontest').controller('TeststepsController', ['$scope', 'Teststeps', '$stateParams', '$timeout',
    '$uibModal', 'IronTestUtils', '$http', 'Environments',
  function($scope, Teststeps, $stateParams, $timeout, $uibModal, IronTestUtils, $http, Environments) {
    $scope.teststep = {
      assertions: []
    };
    $scope.showAssertionsArea = false;

    $scope.teststepNewlyCreated = function() {
      return $stateParams.newlyCreated === true;
    };

    $scope.activeTabIndex = $scope.teststepNewlyCreated() ? 0 : 2;

    var timer;
    //  use object instead of primitives, so that child scope can update the values
    $scope.savingStatus = {};

    $scope.autoSave = function(isValid, successCallback) {
      $scope.savingStatus.changeUnsaved = true;
      if (timer) $timeout.cancel(timer);
      timer = $timeout(function() {
        $scope.update(isValid, successCallback);
      }, 2000);
    };

    $scope.update = function(isValid, successCallback) {
      if (timer) $timeout.cancel(timer);  //  cancel existing timer if the update function is called directly (to avoid duplicate save)
      $scope.savingStatus.changeUnsaved = true;
      if (isValid) {
        //  For DB test step, exclude the result property from the assertions,
        //  as the property does not exist in server side Assertion class
        if ($scope.teststep.type === 'DB') {
          $scope.teststep.assertions.forEach(function(assertion) {
            delete assertion.result;
          });
        }

        $scope.teststep.$update(function(response) {
          $scope.savingStatus.changeUnsaved = false;
          $scope.$broadcast('successfullySaved');
          $scope.teststep = response;
          if (successCallback) {
            successCallback();
          }
        }, function(response) {
          $scope.savingStatus.changeUnsaved = false;
          IronTestUtils.openErrorHTTPResponseModal(response);
        });
      } else {
        $scope.savingStatus.changeUnsaved = false;
        $scope.savingStatus.submitted = true;
      }
    };

    //  This function is used by child controllers to replace the teststep object in this scope.
    $scope.setTeststep = function(teststep) {
      $scope.teststep = teststep;
    };

    $scope.findOne = function() {
      Teststeps.get({
        testcaseId: $stateParams.testcaseId,
        teststepId: $stateParams.teststepId
      }, function (response) {
        $scope.teststep = response;
      }, function(response) {
        IronTestUtils.openErrorHTTPResponseModal(response);
      });
    };

    $scope.selectManagedEndpoint = function() {
      //  find managed endpoints by type
      var endpointType = $scope.teststep.endpoint.type;
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
              endpointType: function () {
                return endpointType;
              },
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
            //  Modal dismissed. Do nothing.
          });
        }, function errorCallback(response) {
          IronTestUtils.openErrorHTTPResponseModal(response);
        });
    };

    $scope.enterShareEndpointMode = function() {
      //  find all environments
      Environments.query(function(environments) {
        if (environments && environments.length > 0) {
          $scope.environments = environments;
          $scope.teststep.endpoint.environment = environments[0];
        } else {
          IronTestUtils.openErrorMessageModal('No environment yet.',
              'To share the endpoint, please create an environment first.');
        }
      }, function(response) {
        IronTestUtils.openErrorHTTPResponseModal(response);
      });
    };

    $scope.isInShareEndpointMode = function() {
      return typeof $scope.environments !== 'undefined';
    };

    $scope.shareEndpoint = function(isValid) {
      //  if successful, this will reload the whole test step
      $scope.update(isValid, function successCallback() {
        //  exit share-endpoint mode
        delete $scope.environments;
      });
    };

    $scope.cancelShareEndpoint = function() {
      //  reload the whole test step
      $scope.findOne();

      //  exit share-endpoint mode
      delete $scope.environments;
    };

    $scope.toggleAssertionsArea = function() {
      if ($scope.showAssertionsArea) {    //  for toggle off
        var elementHeight = document.getElementById('assertionsArea').offsetHeight;
        $scope.$broadcast('elementRemovedFromColumn', { elementHeight: elementHeight });
      }

      $scope.showAssertionsArea = !$scope.showAssertionsArea;
    };

    $scope.assertionsAreaLoadedCallback = function() {
      var elementHeight = document.getElementById('assertionsArea').offsetHeight;
      $scope.$broadcast('elementInsertedIntoColumn', { elementHeight: elementHeight });
    };
  }
]);
