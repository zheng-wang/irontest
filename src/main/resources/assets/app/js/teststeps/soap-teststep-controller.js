'use strict';

//  NOTICE:
//    The $scope here prototypically inherits from the $scope of teststeps-controller.js.
//    ng-include also creates a scope.
angular.module('iron-test').controller('SOAPTeststepController', ['$scope', 'Testruns', 'IronTestUtils',
    '$uibModal',
  function($scope, Testruns, IronTestUtils, $uibModal) {
    $scope.activeTabIndex = $scope.teststepNewlyCreated() ? 0 : 2;

    $scope.tempData = {};
    $scope.showAssertionsArea = false;

    $scope.generateRequest = function() {
      //  open modal dialog
      var modalInstance = $uibModal.open({
        templateUrl: '/ui/views/teststeps/soap/select-soap-operation-modal.html',
        controller: 'SelectSOAPOperationModalController',
        size: 'lg',
        windowClass: 'select-soap-operation-modal',
        resolve: {
          soapAddress: function () {
            return $scope.teststep.endpoint.url;
          }
        }
      });

      //  handle result from modal dialog
      modalInstance.result.then(function (request) {
        //  save the generated request to teststep (in parent scope/controller)
        $scope.teststep.request = request;
        $scope.update(true);  //  save immediately (no timeout)
      }, function () {
        //  Modal dismissed. Do nothing.
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
      }, function(response) {
        IronTestUtils.openErrorHTTPResponseModal(response);
      });
    };

    $scope.assertionsAreaLoadedCallback = function() {
      $scope.$broadcast('assertionsAreaLoaded');
    };
  }
]);
