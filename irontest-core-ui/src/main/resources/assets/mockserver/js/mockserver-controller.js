'use strict';

angular.module('mockserver').controller('MockServerController', ['$scope', 'MockServer', 'IronTestUtils', '$state',
    '$transitions',
  function($scope, MockServer, IronTestUtils, $state, $transitions) {
    $scope.findAllStubInstances = function() {
      MockServer.findAllStubInstances(function(stubInstances) {
        $scope.stubInstances = stubInstances;
      }, function(response) {
        IronTestUtils.openErrorHTTPResponseModal(response);
      });
    };

    var findRequestsForStubInstance = function(stubInstanceId) {
      MockServer.findRequestsForStubInstance({ stubInstanceId: stubInstanceId }, function(stubRequests) {
        $scope.stubRequests = stubRequests;
      }, function(response) {
        IronTestUtils.openErrorHTTPResponseModal(response);
      });
    };

    $scope.stubInstanceSelected = function(stubInstanceId) {
      $scope.selectedStubInstanceId = stubInstanceId;
      $state.go('stub_instance', { stubInstanceId: stubInstanceId });
      findRequestsForStubInstance(stubInstanceId);
    };

    $scope.stubRequestSelected = function(stubRequestId) {
      $scope.selectedStubRequestId = stubRequestId;
      $state.go('stub_request', { stubInstanceId: $scope.selectedStubInstanceId, stubRequestId: stubRequestId });
    };

    //  init things on page refresh
    $transitions.onSuccess({}, function(trans) {
      if (trans.$from().name === '') {
        var stateName = trans.$to().name;
        var stateParams = trans.params('to');
        if (stateName === 'stub_instance' || stateName === 'stub_request') {
          $scope.selectedStubInstanceId = stateParams.stubInstanceId;
          findRequestsForStubInstance(stateParams.stubInstanceId);
        }
        if (stateName === 'stub_request') {
          $scope.selectedStubRequestId = stateParams.stubRequestId;
        }
      }
    });
  }
]);