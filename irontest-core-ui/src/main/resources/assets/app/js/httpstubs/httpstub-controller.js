'use strict';

//  For HTTP stub edit view.
angular.module('irontest').controller('HTTPStubController', ['$scope', 'HTTPStubs', 'IronTestUtils', '$stateParams',
    '$timeout', '$rootScope', 'uiGridConstants',
  function($scope, HTTPStubs, IronTestUtils, $stateParams, $timeout, $rootScope, uiGridConstants) {
    const SPEC_TAB_INDEX = 1;
    $scope.activeTabIndex = SPEC_TAB_INDEX;
    $scope.isStubStateful = false;
    $scope.httpStubModelObject = { requestURL: null };
    $scope.isRequestURLRegexMatching = false;
    var timer;

    $scope.autoSave = function(isValid) {
      if (timer) $timeout.cancel(timer);
      timer = $timeout(function() {
        $scope.update(isValid);
      }, 2000);
    };

    $scope.update = function(isValid, successCallback) {
      if (isValid) {
        $scope.httpStub.$update(function() {
          $scope.$broadcast('successfullySaved');
          if (successCallback) {
            successCallback();
          }
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
        var request = httpStub.spec.request;

        var scenarioName = httpStub.spec.scenarioName
        if (scenarioName && scenarioName.trim() !== '') {
          $scope.isStubStateful = true;
        }

        if (request.urlPattern) {
          $scope.httpStubModelObject.requestURL = request.urlPattern;
          $scope.isRequestURLRegexMatching = true;
        } else {
          $scope.httpStubModelObject.requestURL = request.url;
          $scope.isRequestURLRegexMatching = false;
        }

        $scope.requestBodyMainPattern = IronTestUtils.getRequestBodyMainPattern(request.method, request.bodyPatterns);

        //  set request and response headers
        var requestHeaders = request.headers;
        if (requestHeaders) {
          $scope.requestHeaderGridOptions.data = Object.keys(requestHeaders).map(function(key) {
            var header = requestHeaders[key];
            var operator = Object.keys(header)[0];
            var value = operator === 'anything' ? '' : header[operator];
            return { name: key, operator: operator, value: value };
          });
        }
        var responseHeaders = httpStub.spec.response.headers;
        if (responseHeaders) {
          $scope.responseHeaderGridOptions.data = Object.keys(responseHeaders).map(function(key) {
            return { name: key, value: responseHeaders[key] };
          });
        }
      }, function(response) {
        IronTestUtils.openErrorHTTPResponseModal(response);
      });
    };

    $scope.toggleStubStateful = function(isValid) {
      var stubSpec = $scope.httpStub.spec;
      if ($scope.isStubStateful) {
        stubSpec.scenarioName = null;
        stubSpec.requiredScenarioState = null;
        stubSpec.newScenarioState = null;
        $scope.isStubStateful = false;
      } else {
        stubSpec.scenarioName = 'Scenario 1';
        $scope.isStubStateful = true;
      }
      $scope.update(isValid);
    };

    $scope.requestBodyApplicable = function() {
      if ($scope.httpStub) {     //  when the view is on loading, there is no $scope.httpStub
        return IronTestUtils.requestBodyApplicable($scope.httpStub.spec.request.method);
      } else {
        return true;
      }
    };

    $scope.methodChanged = function(isValid) {
      var request = $scope.httpStub.spec.request;
      if ($scope.requestBodyApplicable()) {
        if (!$scope.requestBodyMainPattern) {
          $scope.requestBodyMainPattern = IronTestUtils.getRequestBodyMainPattern(request.method);
        }
      } else {
        delete request.bodyPatterns;
        delete $scope.requestBodyMainPattern;
      }
      $scope.update(isValid);
    };

    $scope.requestURLChanged = function(isValid) {
      var request = $scope.httpStub.spec.request;
      if ($scope.isRequestURLRegexMatching) {
        request.urlPattern = $scope.httpStubModelObject.requestURL;
      } else {
        request.url = $scope.httpStubModelObject.requestURL;
      }
      $scope.autoSave(isValid);
    };

    $scope.requestBodyMainPatternNameChanged = function(isValid) {
      var newMainPatternName = $scope.requestBodyMainPattern.name;
      var request = $scope.httpStub.spec.request;
      if (newMainPatternName === 'any') {
        delete request.bodyPatterns;
        $scope.requestBodyMainPattern = IronTestUtils.getRequestBodyMainPattern($scope.httpStub.spec.request.method);
      } else {
        request.bodyPatterns = [];
        var bodyPatterns = request.bodyPatterns;
        var bodyPattern = new Object();
        var hiddenMainPatternValue;
        if (newMainPatternName === 'equalToXml') {
          hiddenMainPatternValue = '<IronTest_ToBeSubstitutedDuringStepRun/>';
          bodyPattern.enablePlaceholders = true;
          bodyPattern.placeholderOpeningDelimiterRegex = "#\\{";
        } else if (newMainPatternName === 'equalToJson') {
          hiddenMainPatternValue = "\"IronTest_ToBeSubstitutedDuringStepRun\"";
        }
        bodyPattern[newMainPatternName] = hiddenMainPatternValue;
        bodyPatterns.push(bodyPattern);
      }
      $scope.update(isValid);
    };

    $scope.toggleRequestURLRegexMatching = function(isValid) {
      var request = $scope.httpStub.spec.request;
      if ($scope.isRequestURLRegexMatching) {
        request.url = request.urlPattern;
        delete request.urlPattern;
        $scope.isRequestURLRegexMatching = false;
      } else {
        request.urlPattern = request.url;
        delete request.url;
        $scope.isRequestURLRegexMatching = true;
      }
      $scope.update(isValid);
    };

    $scope.toggleDelayResponse = function(isValid) {
      var response = $scope.httpStub.spec.response;
      if (response.hasOwnProperty('fixedDelayMilliseconds')) {   //  can't use if (response.fixedDelayMilliseconds) here, as it will be true when fixedDelayMilliseconds exists with value 0
        delete response.fixedDelayMilliseconds;
      } else {
        response.fixedDelayMilliseconds = 0;
      }
      $scope.update(isValid);
    };

    var createRequestHeader = function(gridMenuEvent) {
      var headersInGrid = $scope.requestHeaderGridOptions.data;
      var request = $scope.httpStub.spec.request;
      if (!request.headers) {
        request.headers = {};
      }
      var newHeaderName = IronTestUtils.getNextNameInSequence(headersInGrid, 'name');
      var newHeaderValue = '';
      request.headers[newHeaderName] = { equalTo: newHeaderValue };
      headersInGrid.push({ name: newHeaderName, operator: 'equalTo', value: newHeaderValue });
      $scope.update(true, function selectTheNewRow() {
        $scope.requestHeaderGridApi.grid.modifyRows(headersInGrid);
        $scope.requestHeaderGridApi.selection.selectRow(headersInGrid[headersInGrid.length - 1]);
      });
    };

    var deleteRequestHeader = function(gridMenuEvent) {
      var selectedRow = $scope.requestHeaderGridApi.selection.getSelectedRows()[0];
      var headersInGrid = $scope.requestHeaderGridOptions.data;
      var headersObj = $scope.httpStub.spec.request.headers;
      delete headersObj[selectedRow.name];
      IronTestUtils.deleteArrayElementByProperty(headersInGrid, '$$hashKey', selectedRow.$$hashKey);
      $scope.update(true);
    };

    var createResponseHeader = function(gridMenuEvent) {
      var headersInGrid = $scope.responseHeaderGridOptions.data;
      var response = $scope.httpStub.spec.response;
      if (!response.headers) {
        response.headers = {};
      }
      var headersObj = response.headers;
      var newHeaderName = IronTestUtils.getNextNameInSequence(headersInGrid, 'name');
      var newHeaderValue = '';
      headersObj[newHeaderName] = newHeaderValue;
      headersInGrid.push({ name: newHeaderName, value: newHeaderValue });
      $scope.update(true, function selectTheNewRow() {
        $scope.responseHeaderGridApi.grid.modifyRows(headersInGrid);
        $scope.responseHeaderGridApi.selection.selectRow(headersInGrid[headersInGrid.length - 1]);
      });
    };

    var deleteResponseHeader = function(gridMenuEvent) {
      var selectedRow = $scope.responseHeaderGridApi.selection.getSelectedRows()[0];
      var headersInGrid = $scope.responseHeaderGridOptions.data;
      var headersObj = $scope.httpStub.spec.response.headers;
      delete headersObj[selectedRow.name];
      IronTestUtils.deleteArrayElementByProperty(headersInGrid, '$$hashKey', selectedRow.$$hashKey);
      $scope.update(true);
    };

    var commonHeaderGridOptions = {
      enableSorting: false, enableRowHeaderSelection: false, multiSelect: false,
      enableGridMenu: true, enableColumnMenus: false, gridMenuShowHideColumns: false,
      rowHeight: 20, enableHorizontalScrollbar: uiGridConstants.scrollbars.NEVER
    };

    $scope.requestHeaderGridOptions = JSON.parse(JSON.stringify(commonHeaderGridOptions));
    $scope.requestHeaderGridOptions.columnDefs = [
      { name: 'name', width: '25%',
        headerTooltip: 'Double click cell to edit', enableCellEdit: true,
        editableCellTemplate: 'headerGridEditableCellTemplate.html' },
      { name: 'operator', width: '15%', headerTooltip: 'Double click cell to select',
        editableCellTemplate: 'ui-grid/dropdownEditor', editDropdownOptionsArray: [
        { id: 'equalTo', value: 'equalTo' }, { id: 'anything', value: 'anything' }, { id: 'matches', value: 'matches' }
      ]},
      { name: 'value', headerTooltip: 'Double click cell to edit', cellTooltip: true,
        cellEditableCondition: function(scope) {
          return scope.row.entity.operator !== 'anything';
        },
        editableCellTemplate: 'headerGridEditableCellTemplate.html' }
    ];
    $scope.requestHeaderGridOptions.gridMenuCustomItems = [
      { title: 'Create', order: 210, action: createRequestHeader,
        shown: function() {
          return !$rootScope.appStatus.isForbidden();
        }
      },
      { title: 'Delete', order: 220, action: deleteRequestHeader,
        shown: function() {
          return !$rootScope.appStatus.isForbidden() &&
            $scope.requestHeaderGridApi.selection.getSelectedRows().length === 1;
        }
      }
    ];
    $scope.requestHeaderGridOptions.onRegisterApi = function(gridApi) {
      $scope.requestHeaderGridApi = gridApi;
      gridApi.edit.on.afterCellEdit($scope, function(rowEntity, colDef, newValue, oldValue) {
        if (newValue !== oldValue) {
          var request = $scope.httpStub.spec.request;
          var headersObj = request.headers;
          if (colDef.name === 'name') {       //  header name was changed
            //  to preserve the order of headers, can't just create new property and delete old property on headersObj
            request.headers = {};   //  can't use headersObj variable in this if block
            var headersInGrid = $scope.requestHeaderGridOptions.data;
            headersInGrid.forEach(headerInGrid =>
              request.headers[headerInGrid.name] = { [headerInGrid.operator]: headerInGrid.value }
            );
          } else if (colDef.name === 'operator') {           //  header operator was changed
            rowEntity.value = '';
            headersObj[rowEntity.name] = { [newValue]: '' };
          } else if (colDef.name === 'value') {                            //  header value was changed
            headersObj[rowEntity.name][rowEntity.operator] = newValue;
          }
          $scope.update(true);
        }
      });
    };

    $scope.responseHeaderGridOptions = JSON.parse(JSON.stringify(commonHeaderGridOptions));
    $scope.responseHeaderGridOptions.columnDefs = [
      { name: 'name', width: '30%',
        headerTooltip: 'Double click cell to edit', enableCellEdit: true,
        editableCellTemplate: 'headerGridEditableCellTemplate.html' },
      { name: 'value', headerTooltip: 'Double click cell to edit', enableCellEdit: true, cellTooltip: true,
        editableCellTemplate: 'headerGridEditableCellTemplate.html' }
    ];
    $scope.responseHeaderGridOptions.gridMenuCustomItems = [
      { title: 'Create', order: 210, action: createResponseHeader,
        shown: function() {
          return !$rootScope.appStatus.isForbidden();
        }
      },
      { title: 'Delete', order: 220, action: deleteResponseHeader,
        shown: function() {
          return !$rootScope.appStatus.isForbidden() &&
            $scope.responseHeaderGridApi.selection.getSelectedRows().length === 1;
        }
      }
    ];
    $scope.responseHeaderGridOptions.onRegisterApi = function(gridApi) {
      $scope.responseHeaderGridApi = gridApi;
      gridApi.edit.on.afterCellEdit($scope, function(rowEntity, colDef, newValue, oldValue) {
        if (newValue !== oldValue) {
          var response = $scope.httpStub.spec.response;
          var headersObj = response.headers;
          if (colDef.name === 'name') {       //  header name was changed
            //  to preserve the order of headers, can't just create new property and delete old property on headersObj
            response.headers = {};
            var headersInGrid = $scope.responseHeaderGridOptions.data;
            headersInGrid.forEach(headerInGrid => response.headers[headerInGrid.name] = headerInGrid.value);
          } else {                            //  header value was changed
            var headerName = rowEntity.name;
            headersObj[headerName] = newValue;
          }
          $scope.update(true);
        }
      });
    };
  }
]);