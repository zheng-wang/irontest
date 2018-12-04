'use strict';

//  NOTICE:
//    When on the test case edit view, the $scope here prototypically inherits from the $scope of TestcasesController,
angular.module('irontest').controller('HTTPStubsController', ['$scope', 'HTTPStubs', 'IronTestUtils', '$stateParams',
    '$state', '$timeout', '$rootScope',
  function($scope, HTTPStubs, IronTestUtils, $stateParams, $state, $timeout, $rootScope) {
    //  HTTP stubs of the test case
    $scope.httpStubs = [];

    $scope.httpStubGridOptions = {
      data: 'httpStubs', enableColumnMenus: false,
      columnDefs: [
        {
          name: 'number', displayName: 'NO.', width: 55, minWidth: 55,
          cellTemplate: 'httpStubGridNOCellTemplate.html'
        },
        {
          name: 'spec.request.url', displayName: 'URL', width: '50%', cellTemplate:'httpStubGridURLCellTemplate.html'
        },
        {
          name: 'spec.request.method', displayName: 'Method', width: 70, minWidth: 70
        },
        {
          name: 'delete', width: 70, minWidth: 70, enableSorting: false,
          cellTemplate: 'httpStubGridDeleteCellTemplate.html'
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

    $scope.removeHTTPStub = function(httpStub) {
      httpStub.$remove(function(response) {
        IronTestUtils.deleteArrayElementByProperty($scope.httpStubs, 'id', httpStub.id);
      }, function(response) {
        IronTestUtils.openErrorHTTPResponseModal(response);
      });
    };

    var timer;
    $scope.autoSave = function(isValid) {
      if (timer) $timeout.cancel(timer);
      timer = $timeout(function() {
        $scope.update(isValid);
      }, 2000);
    };

    $scope.update = function(isValid) {
      if (isValid) {
        $scope.httpStub.$update(function(response) {
          $scope.$broadcast('successfullySaved');
          $scope.httpStub = response;
        }, function(response) {
          IronTestUtils.openErrorHTTPResponseModal(response);
        });
      } else {
        $scope.submitted = true;
      }
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

    $scope.expectedRequestBodyNotApplicable = function() {
      if ($scope.httpStub) {
        var requestMethod = $scope.httpStub.spec.request.method;
        return $rootScope.appStatus.isForbidden() || requestMethod === 'GET' || requestMethod === 'DELETE';
      } else {
        return true;
      }
    };

    $scope.enableExpectedRequestBody = function() {
      var request = $scope.httpStub.spec.request;
      if (!request.bodyPatterns) {
        request.bodyPatterns = [];
        $scope.update(true);
      }
    };
  }
]);