'use strict';

angular.module('mockserver').controller('MockServerController', ['$scope', 'MockServer', 'IronTestUtils', '$state',
    '$transitions', '$timeout',
  function($scope, MockServer, IronTestUtils, $state, $transitions, $timeout) {
    const DATA_REFRESH_INTERVAL = 1500;   //  in milliseconds
    var findAllStubInstancesTimer;
    var findAllUnmatchedStubRequestsTimer;
    var findMatchedRequestsForSelectedStubInstanceTimer;

    var findAllStubInstances = function() {
      if (findAllStubInstancesTimer) $timeout.cancel(findAllStubInstancesTimer);
      MockServer.findAllStubInstances(function(stubInstances) {
        $scope.stubInstances = stubInstances;

        if ($scope.selectedStubInstanceId && (
            !stubInstances.find(stubInstance => stubInstance.id === $scope.selectedStubInstanceId))) {  //  the selected stub instance no longer exists in the mock server
          $scope.selectedStubInstanceId = null;
          $scope.matchedRequests = null;
          $state.go('home');
        }

        findAllStubInstancesTimer = $timeout(findAllStubInstances, DATA_REFRESH_INTERVAL);
      }, function(response) {
        IronTestUtils.openErrorHTTPResponseModal(response);
      });
    };

    var findAllUnmatchedStubRequests = function() {
      if (findAllUnmatchedStubRequestsTimer) $timeout.cancel(findAllUnmatchedStubRequestsTimer);
      MockServer.findAllUnmatchedStubRequests(function(unmatchedRequests) {
        $scope.unmatchedRequests = unmatchedRequests;

        var selectedUnmatchedStubRequestId = $scope.selectedUnmatchedStubRequestId;
        if (selectedUnmatchedStubRequestId && (!unmatchedRequests.find(unmatchedRequest => unmatchedRequest.id === selectedUnmatchedStubRequestId))) {  //  the selected unmatched stub request no longer exists in the mock server
          $scope.selectedUnmatchedStubRequestId = null;
          $state.go('home');
        }

        findAllUnmatchedStubRequestsTimer = $timeout(findAllUnmatchedStubRequests, DATA_REFRESH_INTERVAL);
      }, function(response) {
        IronTestUtils.openErrorHTTPResponseModal(response);
      });
    };

    var findMatchedRequestsForSelectedStubInstance = function() {
      if (findMatchedRequestsForSelectedStubInstanceTimer) $timeout.cancel(findMatchedRequestsForSelectedStubInstanceTimer);
      var selectedStubInstanceId = $scope.selectedStubInstanceId;
      if (selectedStubInstanceId) {
        MockServer.findMatchedRequestsForStubInstance({ stubInstanceId: selectedStubInstanceId }, function(stubRequests) {
          $scope.matchedRequests = stubRequests;

          var selectedMatchedStubRequestId = $scope.selectedMatchedStubRequestId;
          if (selectedMatchedStubRequestId && (!stubRequests.find(stubRequest => stubRequest.id === selectedMatchedStubRequestId))) {  //  the selected matched stub request no longer exists in the mock server
            $scope.selectedMatchedStubRequestId = null;
            $state.go('stub_instance', { stubInstanceId: selectedStubInstanceId });
          }

          findMatchedRequestsForSelectedStubInstanceTimer = $timeout(findMatchedRequestsForSelectedStubInstance, DATA_REFRESH_INTERVAL);
        }, function(response) {
          IronTestUtils.openErrorHTTPResponseModal(response);
        });
      }
    };

    $scope.findAllStubInstancesAndUnmatchedStubRequests = function() {
      findAllStubInstances();
      findAllUnmatchedStubRequests();
    };

    $scope.stubInstanceSelected = function(stubInstanceId) {
      $scope.selectedStubInstanceId = stubInstanceId;
      $scope.selectedMatchedStubRequestId = null;
      $scope.selectedUnmatchedStubRequestId = null;
      $state.go('stub_instance', { stubInstanceId: stubInstanceId });
      findMatchedRequestsForSelectedStubInstance(stubInstanceId);
    };

    $scope.matchedStubRequestSelected = function(stubRequestId) {
      $scope.selectedMatchedStubRequestId = stubRequestId;
      $scope.selectedUnmatchedStubRequestId = null;
      $state.go('matched_stub_request', { stubInstanceId: $scope.selectedStubInstanceId, stubRequestId: stubRequestId });
    };

    $scope.unmatchedStubRequestSelected = function(stubRequestId) {
      $scope.selectedUnmatchedStubRequestId = stubRequestId;
      $scope.selectedMatchedStubRequestId = null;
      $scope.selectedStubInstanceId = null;
      $scope.matchedRequests = null;
      $state.go('unmatched_stub_request', { stubRequestId: stubRequestId });
    };

    $scope.clearRequestLog = function() {
      MockServer.clearRequestLog(function() {
        findMatchedRequestsForSelectedStubInstance();
        findAllUnmatchedStubRequests();
      }, function(response) {
        IronTestUtils.openErrorHTTPResponseModal(response);
      });
    };

    //  init things on page refresh
    $transitions.onSuccess({}, function(trans) {
      if (trans.$from().name === '') {
        var stateName = trans.$to().name;
        var stateParams = trans.params('to');
        if (stateName === 'stub_instance' || stateName === 'matched_stub_request') {
          $scope.selectedStubInstanceId = stateParams.stubInstanceId;
          findMatchedRequestsForSelectedStubInstance();
        }
        if (stateName === 'matched_stub_request') {
          $scope.selectedMatchedStubRequestId = stateParams.stubRequestId;
        }
        if (stateName === 'unmatched_stub_request') {
          $scope.selectedUnmatchedStubRequestId = stateParams.stubRequestId;
        }
      }
    });
  }
]);