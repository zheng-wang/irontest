'use strict';

angular.module('mockserver').controller('MockServerController', ['$scope', 'MockServer', 'IronTestUtils', '$state',
    '$transitions',
  function($scope, MockServer, IronTestUtils, $state, $transitions) {
    $scope.findAllStubInstancesAndUnmatchedStubRequests = function() {
      MockServer.findAllStubInstances(function(stubInstances) {
        $scope.stubInstances = stubInstances;
      }, function(response) {
        IronTestUtils.openErrorHTTPResponseModal(response);
      });

      MockServer.findAllUnmatchedStubRequests(function(unmatchedRequests) {
        $scope.unmatchedRequests = unmatchedRequests;
      }, function(response) {
        IronTestUtils.openErrorHTTPResponseModal(response);
      });
    };

    var findMatchedRequestsForStubInstance = function(stubInstanceId) {
      MockServer.findMatchedRequestsForStubInstance({ stubInstanceId: stubInstanceId }, function(stubRequests) {
        $scope.matchedRequests = stubRequests;
      }, function(response) {
        IronTestUtils.openErrorHTTPResponseModal(response);
      });
    };

    $scope.stubInstanceSelected = function(stubInstanceId) {
      $scope.selectedStubInstanceId = stubInstanceId;
      $scope.selectedStubRequestId = null;
      $state.go('stub_instance', { stubInstanceId: stubInstanceId });
      findMatchedRequestsForStubInstance(stubInstanceId);
    };

    $scope.matchedStubRequestSelected = function(stubRequestId) {
      $scope.selectedStubRequestId = stubRequestId;
      $state.go('matched_stub_request', { stubInstanceId: $scope.selectedStubInstanceId, stubRequestId: stubRequestId });
    };

    $scope.unmatchedStubRequestSelected = function(stubRequestId) {
      $scope.selectedStubRequestId = stubRequestId;
      $scope.selectedStubInstanceId = null;
      $scope.matchedRequests = null;
      $state.go('unmatched_stub_request', { stubRequestId: stubRequestId });
    };

    //  init things on page refresh
    $transitions.onSuccess({}, function(trans) {
      if (trans.$from().name === '') {
        var stateName = trans.$to().name;
        var stateParams = trans.params('to');
        if (stateName === 'stub_instance' || stateName === 'matched_stub_request') {
          $scope.selectedStubInstanceId = stateParams.stubInstanceId;
          findMatchedRequestsForStubInstance(stateParams.stubInstanceId);
        }
        if (stateName === 'matched_stub_request' || stateName === 'unmatched_stub_request') {
          $scope.selectedStubRequestId = stateParams.stubRequestId;
        }
      }
    });
  }
]);