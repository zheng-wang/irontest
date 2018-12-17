'use strict';

angular.module('mockserver').controller('StubRequestController', ['$scope', 'MockServer', 'IronTestUtils',
    '$stateParams',
  function($scope, MockServer, IronTestUtils, $stateParams) {
    //  create formatted headers string
    var formatHTTPHeadersObj = function(headersObj) {
      var result = '';
      Object.keys(headersObj).forEach(function(key, index) {
        if (index > 0) {
          result += '\n';
        }
        result += key + ': ' + headersObj[key];
      });

      return result;
    };

    $scope.find = function() {
      MockServer.findStubRequestById({ stubRequestId: $stateParams.stubRequestId }, function(stubRequest) {
        $scope.stubRequest = stubRequest;

        $scope.stubRequestHeadersStr = formatHTTPHeadersObj(stubRequest.request.headers);
        $scope.stubResponseHeadersStr = formatHTTPHeadersObj(stubRequest.response.headers);
      }, function(response) {
        IronTestUtils.openErrorHTTPResponseModal(response);
      });
    };
  }
]);