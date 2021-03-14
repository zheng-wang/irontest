'use strict';

//  NOTICE:
//    The $scope here prototypically inherits from the $scope of TeststepsController.
//    ng-include also creates a scope.
angular.module('irontest').controller('TeststepsEndpointController', ['$scope',
    '$uibModal', 'IronTestUtils', 'Environments', 'Teststeps',
  function($scope, $uibModal, IronTestUtils, Environments, Teststeps) {
    $scope.selectManagedEndpoint = function() {
      var endpointType = $scope.teststep.endpoint.type;

      //  open modal dialog
      var modalInstance = $uibModal.open({
        templateUrl: '/ui/views/endpoints/list-modal.html',
        controller: 'SelectManagedEndpointModalController',
        size: 'lg',
        windowClass: 'select-managed-endpoint-modal',
        resolve: {
          endpointType: function() {
            return endpointType;
          },
          titleSuffix: function() {
            return '';
          }
        }
      });

      //  handle result from modal dialog
      modalInstance.result.then(function closed(selectedEndpoint) {
        $scope.teststep.endpoint = selectedEndpoint;
        $scope.update(true);  //  save immediately (no timeout)
      }, function dismissed() {
        //  Modal dismissed. Do nothing.
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

    $scope.unmanageEndpoint = function() {
      var teststep = new Teststeps({
        id: $scope.teststep.id,
        testcaseId: $scope.teststep.testcaseId
      });
      teststep.$unmanageEndpoint(function(response) {
        $scope.$emit('successfullySaved');
        $scope.setTeststep(response);
      }, function(error) {
        IronTestUtils.openErrorHTTPResponseModal(error);
      });
    };

    $scope.mqEndpointConnectionModeChanged = function(isValid) {
      //  clear properties for Client connection mode, to avoid saving unintended values
      var endpoint = $scope.teststep.endpoint;
      var endpointProperties = endpoint.otherProperties;
      endpoint.host = null;
      endpoint.port = null;
      endpointProperties.svrConnChannelName = null;

      if (!$scope.isInShareEndpointMode()) {
        //  update test step immediately (no timeout)
        $scope.update(isValid);
      }
    };

    $scope.useEndpointProperty = function() {
      var teststep = new Teststeps({
        id: $scope.teststep.id,
        testcaseId: $scope.teststep.testcaseId,
        endpoint: { id: $scope.teststep.endpoint.id }
      });
      teststep.$useEndpointProperty(function(response) {
        //  exit share-endpoint mode (if in the mode)
        delete $scope.environments;
        $scope.$emit('successfullySaved');
        $scope.setTeststep(response);
      }, function(error) {
        IronTestUtils.openErrorHTTPResponseModal(error);
      });
    };

    $scope.useDirectEndpoint = function() {
      var teststep = new Teststeps({
        id: $scope.teststep.id,
        testcaseId: $scope.teststep.testcaseId,
        type: $scope.teststep.type,
        otherProperties: {}  //  adding this property here to avoid Jackson 'Missing property' error (http://stackoverflow.com/questions/28089484/deserialization-with-jsonsubtypes-for-no-value-missing-property-error)
      });
      teststep.$useDirectEndpoint(function(response) {
        $scope.$emit('successfullySaved');
        $scope.setTeststep(response);
      }, function(error) {
        IronTestUtils.openErrorHTTPResponseModal(error);
      });
    };
  }
]);
