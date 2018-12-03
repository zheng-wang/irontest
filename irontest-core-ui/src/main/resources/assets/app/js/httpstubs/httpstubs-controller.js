'use strict';

//  NOTICE:
//    The $scope here prototypically inherits from the $scope of TestcasesController,
angular.module('irontest').controller('HTTPStubsController', ['$scope', 'HTTPStubs', 'IronTestUtils', '$stateParams',
    '$state',
  function($scope, HTTPStubs, IronTestUtils, $stateParams, $state) {
    //  HTTP stubs of the test case
    $scope.httpStubs = [];

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
      HTTPStubs.query({ testcaseId: $stateParams.testcaseId }, function(httpStubs) {
        $scope.httpStubs = httpStubs;
      }, function(response) {
        IronTestUtils.openErrorHTTPResponseModal(response);
      });
    };

    $scope.createHTTPStub = function() {
      var httpStub = new HTTPStubs();
      httpStub.$save({ testcaseId: $stateParams.testcaseId }, function(httpStub) {
        $state.go('httpstub_edit', {testcaseId: $stateParams.testcaseId, httpStubId: httpStub.id});
      }, function(response) {
        IronTestUtils.openErrorHTTPResponseModal(response);
      });
    };

    $scope.findOne = function() {
      HTTPStubs.get({
        testcaseId: $stateParams.testcaseId, httpStubId: $stateParams.httpStubId
      }, function(httpStub) {
        $scope.httpStub = httpStub;
        var bodyPatterns = httpStub.spec.request.bodyPatterns;
        if (bodyPatterns) {
          for (var i = 0; i < bodyPatterns.length; i++) {
            var bodyPattern = bodyPatterns[i];
            if ('equalToXml' in bodyPattern) {
              $scope.expectedRequestBodyMainPattern = 'equalToXml';
              $scope.expectedRequestBodyMainPatternValue = bodyPattern.equalToXml;
              break;
            } else if ('equalToJson' in bodyPattern) {
              $scope.expectedRequestBodyMainPattern = 'equalToJson';
              $scope.expectedRequestBodyMainPatternValue = bodyPattern.equalToJson;
              break;
            }
          }
        }
      }, function(response) {
        IronTestUtils.openErrorHTTPResponseModal(response);
      });
    };
  }
]);