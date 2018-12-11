'use strict';

angular.module('mockserver').controller('MockServerController', ['$scope', 'MockServer', 'IronTestUtils', '$state',
  function($scope, MockServer, IronTestUtils, $state) {
    $scope.findAllStubInstances = function() {
      MockServer.findAllStubInstances(function(stubInstances) {
        $scope.stubInstances = stubInstances;
      }, function(response) {
        IronTestUtils.openErrorHTTPResponseModal(response);
      });
    };

    $scope.stubInstanceSelected = function(stubInstance) {
      $scope.selectedStubInstance = stubInstance;
      $state.go('stub_instance', { stubInstanceId: stubInstance.id });
      MockServer.findRequestsForStubInstance({ stubInstanceId: stubInstance.id }, function(stubRequests) {
        $scope.stubRequests = stubRequests;
      }, function(response) {
        IronTestUtils.openErrorHTTPResponseModal(response);
      });
    };

    $scope.stubRequestSelected = function(stubRequest) {
       $scope.selectedStubRequest = stubRequest;
       //$state.go('stub_request', { stubRequestId: stubRequest.id });
     };
  }
]);