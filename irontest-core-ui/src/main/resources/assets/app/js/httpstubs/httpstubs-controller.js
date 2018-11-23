'use strict';

//  NOTICE:
//    The $scope here prototypically inherits from the $scope of TestcasesController,
angular.module('irontest').controller('HTTPStubsController', ['$scope', 'HTTPStubs', 'IronTestUtils', '$stateParams',
  function($scope, HTTPStubs, IronTestUtils, $stateParams) {
    //  HTTP stubs of the test case
    $scope.httpStubs = [];

    $scope.httpStubGridOptions = {
      data: 'httpStubs', enableColumnMenus: false,
      columnDefs: [
        {
          name: 'number', displayName: 'NO.', width: '5%'
        },
        {
          name: 'spec.request.url', displayName: 'URL', width: '50%'
        },
        {
          name: 'spec.request.method', displayName: 'Method', width: '5%'
        }
      ]
    };

    $scope.findByTestcaseId = function() {
      HTTPStubs.query({ testcaseId: $stateParams.testcaseId }, function(returnHTTPStubs) {
        $scope.httpStubs = returnHTTPStubs;
        $scope.httpStubs.forEach(function(httpStub) {
          httpStub.spec = JSON.parse(httpStub.specJson);
        });
      }, function(response) {
        IronTestUtils.openErrorHTTPResponseModal(response);
      });
    };
  }
]);