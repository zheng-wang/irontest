'use strict';

//  NOTICE:
//    The $scope here prototypically inherits from the $scope of TeststepsController.
//    ng-include also creates a scope.
angular.module('irontest').controller('SOAPTeststepController', ['$scope', 'Teststeps', 'IronTestUtils',
    '$uibModal',
  function($scope, Teststeps, IronTestUtils, $uibModal) {
    $scope.steprun = {};

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
      var teststep = new Teststeps($scope.teststep);
      teststep.$run(function(response) {
        $scope.steprun.response = response.httpResponseBody;
      }, function(response) {
        IronTestUtils.openErrorHTTPResponseModal(response);
      });
    };
  }
]);
