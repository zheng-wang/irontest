'use strict';

angular.module('iron-test').controller('SOAPTeststepEditController', ['$scope', 'Testruns', '$state', '$timeout',
    '$uibModal',
  function($scope, Testruns, $state, $timeout, $uibModal) {
    var timer;
    //  use object instead of primitives, so that child scope can update the values
    $scope.savingStatus = {
      saveSuccessful: null,
      savingErrorMessage: null
    };
    $scope.tempData = {};
    $scope.showAssertionsArea = false;

    $scope.update = function(isValid) {
      if (isValid) {
        $scope.$parent.teststep.$update(function(response) {
          $scope.savingStatus.saveSuccessful = true;
          $scope.$parent.teststep = response;
        }, function(error) {
          $scope.savingStatus.savingErrorMessage = error.data.message;
          $scope.savingStatus.saveSuccessful = false;
        });
      } else {
        $scope.savingStatus.submitted = true;
      }
    };

    $scope.autoSave = function(isValid) {
      if (timer) $timeout.cancel(timer);
      timer = $timeout(function() {
        $scope.update(isValid);
      }, 2000);
    };

    $scope.selectManagedEndpoint = function() {
      var modalInstance = $uibModal.open({
        templateUrl: '/ui/views/endpoints/list-modal.html',
        controller: 'EndpointsModalController',
        size: 'lg',
        resolve: {
          endpointType: function () {
            return 'SOAP';
          }
        }
      });

      modalInstance.result.then(function (selectedEndpoint) {
        $scope.$parent.teststep.endpoint = selectedEndpoint;
        $scope.autoSave(true);
      }, function () {
        //  Modal dismissed
      });
    };

    $scope.invoke = function() {
      var testrun = {
        teststepId: $scope.$parent.teststep.id,
        request: $scope.$parent.teststep.request
      };
      var testrunRes = new Testruns(testrun);
      testrunRes.$save(function(response) {
        $scope.tempData.soapResponse = response.response;
      }, function(error) {
        alert('Error');
      });
    };

    $scope.assertionsAreaLoadedCallback = function() {
      $scope.$broadcast('assertionsAreaLoaded');
    };
  }
]);
