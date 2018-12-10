'use strict';

angular.module('mockserver').controller('MockServerController', ['$scope', 'MockServer',
  function($scope, MockServer) {
    $scope.findAllStubInstances = function() {
      MockServer.findAllStubInstances(function(stubInstances) {
        $scope.stubInstances = stubInstances;
      }, function(response) {
        alert('error');
        //IronTestUtils.openErrorHTTPResponseModal(response);
      });
    };
  }
]);