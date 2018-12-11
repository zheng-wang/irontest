'use strict';

angular.module('mockserver').controller('StubInstanceController', ['$scope', 'MockServer', 'IronTestUtils',
    '$stateParams',
  function($scope, MockServer, IronTestUtils, $stateParams) {
    $scope.find = function() {
      MockServer.findStubInstanceById({ stubInstanceId: $stateParams.stubInstanceId }, function(stubInstance) {
        $scope.stubInstance = stubInstance;
      }, function(response) {
        IronTestUtils.openErrorHTTPResponseModal(response);
      });
    };
  }
]);