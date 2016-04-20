'use strict';

//  NOTICE:
//    The $scope here prototypically inherits from the $scope of teststeps-controller.js.
//    ng-include also creates a scope.
angular.module('iron-test').controller('SOAPTeststepEditController', ['$scope', 'Testruns', '$state', '$uibModal',
  function($scope, Testruns, $state, $uibModal) {
    $scope.tempData = {};
    $scope.showAssertionsArea = false;

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
        $scope.teststep.endpoint = selectedEndpoint;
        $scope.autoSave(true);
      }, function () {
        //  Modal dismissed
      });
    };

    $scope.invoke = function() {
      var testrun = {
        teststepId: $scope.teststep.id,
        request: $scope.teststep.request
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
