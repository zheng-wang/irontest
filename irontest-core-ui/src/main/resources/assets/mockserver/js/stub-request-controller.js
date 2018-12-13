'use strict';

angular.module('mockserver').controller('StubRequestController', ['$scope', 'MockServer', 'IronTestUtils',
    '$stateParams',
  function($scope, MockServer, IronTestUtils, $stateParams) {
    $scope.find = function() {
      MockServer.findStubRequestById({ stubRequestId: $stateParams.stubRequestId }, function(stubRequest) {
        $scope.stubRequest = stubRequest;
      }, function(response) {
        IronTestUtils.openErrorHTTPResponseModal(response);
      });
    };
  }
]);