'use strict';

//  NOTICE:
//    When on the test case edit view, the $scope here prototypically inherits from the $scope of TestcasesController,
angular.module('irontest').controller('HTTPStubsController', ['$scope', 'HTTPStubs', 'IronTestUtils', '$stateParams',
    '$state', '$timeout', '$rootScope',
  function($scope, HTTPStubs, IronTestUtils, $stateParams, $state, $timeout, $rootScope) {
    //  -------------------------------  for HTTP stub grid on test case edit view -----------------------------------
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
      ],
      onRegisterApi: function(gridApi) {
        $scope.gridApi = gridApi;

        $scope.$parent.handleTestcaseRunResultOutlineAreaDisplay();
      }
    };

    $scope.$on('testcaseRunResultOutlineAreaShown', function() {
      if ($scope.gridApi) {
        $scope.gridApi.core.handleWindowResize();
      }
    });

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

    //  -------------------------------  for HTTP stub edit view -----------------------------------
    var resetRequestBodyMainPattern = function() {
      $scope.requestBodyMainPattern = { name: null, value: null };
    };
    resetRequestBodyMainPattern();

    var timer;
    $scope.autoSave = function(isValid) {
      if (timer) $timeout.cancel(timer);
      timer = $timeout(function() {
        $scope.update(isValid);
      }, 2000);
    };

    $scope.update = function(isValid) {
      if (isValid) {
        $scope.httpStub.$update(function() {
          $scope.$broadcast('successfullySaved');
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
          var bodyPattern = bodyPatterns[0];
          if (bodyPattern) {
            if ('equalToXml' in bodyPattern) {
              $scope.requestBodyMainPattern.name = 'equalToXml';
              $scope.requestBodyMainPattern.value = bodyPattern.equalToXml;
            } else if ('equalToJson' in bodyPattern) {
              $scope.requestBodyMainPattern.name = 'equalToJson';
              $scope.requestBodyMainPattern.value = bodyPattern.equalToJson;
            }
          }
        }
      }, function(response) {
        IronTestUtils.openErrorHTTPResponseModal(response);
      });
    };

    $scope.requestBodyNotApplicable = function() {
      if ($scope.httpStub) {     //  when the view is on loading, there is no $scope.httpStub
        var requestMethod = $scope.httpStub.spec.request.method;
        return $rootScope.appStatus.isForbidden() || requestMethod === 'GET' || requestMethod === 'DELETE';
      } else {
        return true;
      }
    };

    $scope.methodChanged = function(isValid) {
      var request = $scope.httpStub.spec.request;
      if (request.bodyPatterns && $scope.requestBodyNotApplicable()) {
        delete request.bodyPatterns;
        resetRequestBodyMainPattern();
      }
      $scope.update(isValid);
    };

    $scope.toggleRestrictRequestBody = function(isValid) {
      var request = $scope.httpStub.spec.request;
      if (request.bodyPatterns) {
        delete request.bodyPatterns;
      } else {
        request.bodyPatterns = [];
      }
      resetRequestBodyMainPattern();
      $scope.update(isValid);
    };

    $scope.requestBodyMainPatternNameChanged = function(isValid) {
      var newMainPatternName = $scope.requestBodyMainPattern.name;
      var bodyPatterns = $scope.httpStub.spec.request.bodyPatterns;
      bodyPatterns.length = 0;    //  clear the bodyPatterns array
      var bodyPattern = new Object();
      var newMainPatternValue;
      if (newMainPatternName === 'equalToXml') {
        newMainPatternValue = '<a/>';
      } else if (newMainPatternName === 'equalToJson') {
        newMainPatternValue = null;
      }
      $scope.requestBodyMainPattern.value = newMainPatternValue;
      bodyPattern[newMainPatternName] = newMainPatternValue;
      bodyPatterns.push(bodyPattern);
      $scope.update(isValid);
    };

    $scope.requestBodyMainPatternValueChanged = function(isValid) {
      var mainPatternName = $scope.requestBodyMainPattern.name;
      var mainPatternValue = $scope.requestBodyMainPattern.value;
      var bodyPattern = $scope.httpStub.spec.request.bodyPatterns[0];
      bodyPattern[mainPatternName] = mainPatternValue;
      $scope.autoSave(isValid);
    };
  }
]);