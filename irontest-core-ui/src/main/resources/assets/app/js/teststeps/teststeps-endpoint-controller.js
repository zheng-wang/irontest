'use strict';

//  NOTICE:
//    The $scope here prototypically inherits from the $scope of TeststepsController.
//    ng-include also creates a scope.
angular.module('irontest').controller('TeststepsEndpointController', ['$scope',
    '$uibModal', 'IronTestUtils', 'Environments', 'ManagedEndpoints',
  function($scope, $uibModal, IronTestUtils, Environments, ManagedEndpoints) {
    $scope.selectManagedEndpoint = function() {
      //  find managed endpoints by type
      var endpointType = $scope.teststep.endpoint.type;
      ManagedEndpoints.query({ type: endpointType },
        function successCallback(response) {
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
              endpoints: function() {
                return response;
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

    $scope.mqEndpointConnectionModeChanged = function(isValid) {
      var endpointProperties = $scope.teststep.endpoint.otherProperties;
      endpointProperties.host = null;
      endpointProperties.port = null;
      endpointProperties.svrConnChannelName = null;

      if (!$scope.isInShareEndpointMode()) {
        //  update test step immediately (no timeout)
        $scope.update(isValid);
      }
    };
  }
]);
