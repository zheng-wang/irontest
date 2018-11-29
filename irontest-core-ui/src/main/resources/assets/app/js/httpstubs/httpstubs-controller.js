'use strict';

//  NOTICE:
//    The $scope here prototypically inherits from the $scope of TestcasesController,
angular.module('irontest').controller('HTTPStubsController', ['$scope', 'HTTPStubs', 'IronTestUtils', '$stateParams',
  function($scope, HTTPStubs, IronTestUtils, $stateParams) {
    //  HTTP stubs of the test case
    $scope.httpStubs = [];

    $scope.expectedRequestBodyMainPattern = 'abc';

    $scope.httpStubGridOptions = {
      data: 'httpStubs', enableColumnMenus: false,
      columnDefs: [
        {
          name: 'number', displayName: 'NO.', width: '5%'
        },
        {
          name: 'spec.request.url', displayName: 'URL', width: '50%', cellTemplate:'httpStubGridURLCellTemplate.html'
        },
        {
          name: 'spec.request.method', displayName: 'Method', width: '5%'
        }
      ]
    };

    $scope.findByTestcaseId = function() {
      HTTPStubs.query({ testcaseId: $stateParams.testcaseId }, function(returnHTTPStubs) {
        $scope.httpStubs = returnHTTPStubs;
      }, function(response) {
        IronTestUtils.openErrorHTTPResponseModal(response);
      });
    };

    $scope.findOne = function() {
      HTTPStubs.get({
        testcaseId: $stateParams.testcaseId, httpStubId: $stateParams.httpStubId
      }, function(httpStub) {
        $scope.httpStub = httpStub;
        var mainBodyPattern = httpStub.spec.request.bodyPatterns.find(e => ('equalToXml' in e || 'equalToJson' in e));
        $scope.expectedRequestBodyMainPattern = mainBodyPattern.equalToXml || mainBodyPattern.equalToJson;
      }, function(response) {
        IronTestUtils.openErrorHTTPResponseModal(response);
      });
    };

    $scope.showExpectedRequestBodyTextArea = function() {
      var httpStub = $scope.httpStub;
      if (typeof httpStub === 'undefined') {    //  the stub hasn't been loaded into $scope (by the findOne function)
        return false;
      } else {
        return httpStub.spec.request.bodyPatterns.some(e => 'equalToXml' in e || 'equalToJson' in e);
      }
    }
  }
]);