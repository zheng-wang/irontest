'use strict';

angular.module('mockserver').controller('StubInstanceController', ['$scope', 'MockServer', 'IronTestUtils',
    '$stateParams',
  function($scope, MockServer, IronTestUtils, $stateParams) {
    $scope.find = function() {
      MockServer.findStubInstanceById({ stubInstanceId: $stateParams.stubInstanceId }, function(stubInstance, responseHeadersFn, statusCode, statusText) {
        if (statusCode === 204) {    //  on 204 returned, stubInstance is a promise instead of null
          $scope.stubInstance = null;
        } else {
          $scope.stubInstance = stubInstance;
          $scope.requestBodyMainPattern = IronTestUtils.getRequestBodyMainPattern(stubInstance.request.bodyPatterns);

          //  construct stubRequestHeadersStr
          var stubRequestHeadersStr = '';
          var requestHeaders = stubInstance.request.headers;
          if (requestHeaders) {
            Object.keys(requestHeaders).forEach(function(key, index) {
              if (index > 0) {
                stubRequestHeadersStr += '\n';
              }
              stubRequestHeadersStr += key + ': ' + requestHeaders[key].equalTo;
            });
          }
          $scope.stubRequestHeadersStr = stubRequestHeadersStr;

          $scope.stubResponseHeadersStr = IronTestUtils.formatHTTPHeadersObj(stubInstance.response.headers);
        }
      }, function(response) {
        IronTestUtils.openErrorHTTPResponseModal(response);
      });
    };
  }
]);