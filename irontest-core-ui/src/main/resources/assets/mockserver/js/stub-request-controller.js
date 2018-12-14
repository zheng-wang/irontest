'use strict';

angular.module('mockserver').controller('StubRequestController', ['$scope', 'MockServer', 'IronTestUtils',
    '$stateParams',
  function($scope, MockServer, IronTestUtils, $stateParams) {
    $scope.find = function() {
      MockServer.findStubRequestById({ stubRequestId: $stateParams.stubRequestId }, function(stubRequest) {
        $scope.stubRequest = stubRequest;

        //  create formatted request headers string
        var requestHeaders = stubRequest.request.headers;
        $scope.stubRequestHeadersStr = '';
        Object.keys(requestHeaders).forEach(function(key, index) {
          if (index > 0) {
            $scope.stubRequestHeadersStr += '\n';
          }
          $scope.stubRequestHeadersStr += key + ': ' + requestHeaders[key];
        });
      }, function(response) {
        IronTestUtils.openErrorHTTPResponseModal(response);
      });
    };
  }
]);