'use strict';

//  if unspecified, all grid config is for the assertions grid
angular.module('service-testing-tool').controller('AssertionsController', ['$scope', 'Assertions',
    '$stateParams', 'uiGridConstants', 'uiGridEditConstants', '$timeout', 'STTUtils', '$http',
  function($scope, Assertions, $stateParams, uiGridConstants, uiGridEditConstants, $timeout, STTUtils, $http) {
    //  use assertionsModelObj for all variables in the scope, to avoid conflict with parent scope
    $scope.assertionsModelObj = {};

    $scope.assertionsModelObj.tempData = {};

    var timer;

    //  remove currently selected assertion
    var removeCurrentAssertion = function(gridMenuEvent) {
      var currentAssertion = $scope.assertionsModelObj.assertion;
      if (currentAssertion) {
        currentAssertion.$remove({
          testcaseId: $stateParams.testcaseId,
          teststepId: $stateParams.teststepId
        }, function(response) {
          //  delete the assertion row from the grid
          var gridData = $scope.assertionsModelObj.gridOptions.data;
          var indexOfRowToBeDeleted = STTUtils.indexOfArrayElementByProperty(gridData, 'id', currentAssertion.id);
          gridData.splice(indexOfRowToBeDeleted, 1);

          //  set current assertion to null
          $scope.assertionsModelObj.assertion = null;
        }, function(error) {
          alert('Error');
        });
      }
    };

    $scope.assertionsModelObj.gridOptions = {
      enableRowHeaderSelection: false, multiSelect: false, noUnselect: true, enableGridMenu: true,
      columnDefs: [
        {
          name: 'name', width: 250, minWidth: 250, headerTooltip: 'Double click to edit',
          sort: { direction: uiGridConstants.ASC, priority: 1 },
          enableCellEdit: true, editableCellTemplate: 'assertionGridNameEditableCellTemplate.html'
        },
        {name: 'type', width: 80, minWidth: 80, enableCellEdit: false}
      ],
      gridMenuCustomItems: [
        { title: 'Delete', order: 210, action: removeCurrentAssertion }
      ],
      onRegisterApi: function (gridApi) {
        $scope.assertionsModelObj.gridApi = gridApi;
        gridApi.selection.on.rowSelectionChanged($scope, function(row) {
          $scope.assertionsModelObj.assertion = row.entity;
        });
      }
    };

    $scope.assertionsModelObj.findAll = function() {
      Assertions.query(
        {
          testcaseId: $stateParams.testcaseId,
          teststepId: $stateParams.teststepId
        }, function(response) {
          $scope.assertionsModelObj.gridOptions.data = response;
        }, function(error) {
          alert('Error');
        });
    };

    var createAssertion = function(assertion) {
      assertion.$save({
        testcaseId: $stateParams.testcaseId,
        teststepId: $stateParams.teststepId
      }, function(response) {
        $scope.assertionsModelObj.assertion = response;
        var gridData = $scope.assertionsModelObj.gridOptions.data;

        //  add the new assertion to the grid data
        gridData.push(response);

        //  select the new assertion in the grid
        var indexOfNewRow = STTUtils.indexOfArrayElementByProperty(
          gridData, 'id', $scope.assertionsModelObj.assertion.id);
        $timeout(function() {    //  a trick for newly loaded grid data
          $scope.assertionsModelObj.gridApi.selection.selectRow(gridData[indexOfNewRow]);
        });
      }, function(error) {
        alert('Error');
      });
    };

    $scope.assertionsModelObj.createContainsAssertion = function() {
      var assertion = new Assertions({
        teststepId: $stateParams.teststepId,
        name: 'Response contains value',
        type: 'Contains',
        properties: { contains: 'value' }
      });
      createAssertion(assertion);
    };

    $scope.assertionsModelObj.createXPathAssertion = function() {
      var assertion = new Assertions({
        teststepId: $stateParams.teststepId,
        name: 'XPath evaluates to value',
        type: 'XPath',
        properties: {
         xPath: 'true()',
         expectedValue: 'true',
         namespacePrefixes: []
        }
      });

      createAssertion(assertion);
    };

    $scope.assertionsModelObj.update = function(isValid) {
      if (isValid) {
        $scope.assertionsModelObj.assertion.$update({
          testcaseId: $stateParams.testcaseId,
          teststepId: $stateParams.teststepId
        }, function(response) {
          $scope.$parent.savingStatus.saveSuccessful = true;
          $scope.assertionsModelObj.assertion = response;
        }, function(error) {
          $scope.$parent.savingStatus.savingErrorMessage = error.data.message;
          $scope.$parent.savingStatus.saveSuccessful = false;
        });
      } else {
        $scope.$parent.savingStatus.submitted = true;
      }
    };

    //  update assertion without validating whole form and displaying saving successful message
    var assertionUpdateInBackground = function() {
      $scope.assertionsModelObj.assertion.$update({
        testcaseId: $stateParams.testcaseId,
        teststepId: $stateParams.teststepId
      }, function(response) {
        $scope.assertionsModelObj.assertion = response;
      }, function(error) {
        alert('Error');
      });
    };

    $scope.assertionsModelObj.autoSave = function(isValid) {
      if (timer) $timeout.cancel(timer);
      timer = $timeout(function() {
        $scope.assertionsModelObj.update(isValid);
      }, 2000);
    };

    //  evaluate xpath against the input xml
    $scope.assertionsModelObj.evaluateXPath = function(xpath, input) {
      var url = 'api/evaluator';
      $http
        .post(url, {
          type: 'XPath',
          expression: xpath,
          input: input
        })
        .success(function(data, status) {
          $scope.assertionsModelObj.tempData.actualValue = data.result;
        })
        .error(function(data, status) {
          alert('Error');
        });
    };

    var createNamespacePrefix = function(gridMenuEvent) {
      $scope.assertionsModelObj.assertion.properties.namespacePrefixes.push(
        { prefix: 'ns1', namespace: 'http://com.mycompany/service1' }
      );
      assertionUpdateInBackground();
    };

    var removeNamespacePrefix = function(gridMenuEvent) {
      var selectedRows = $scope.assertionsModelObj.xPathNamespacePrefixGridApi.selection.getSelectedRows();
      var namespacePrefixes = $scope.assertionsModelObj.assertion.properties.namespacePrefixes;
      for (var i = 0; i < selectedRows.length; i += 1) {
        var indexOfRowToBeDeleted = STTUtils.indexOfArrayElementByProperty(
          namespacePrefixes, '$$hashKey', selectedRows[i].$$hashKey);
        namespacePrefixes.splice(indexOfRowToBeDeleted, 1);
      }
      assertionUpdateInBackground();
    };

    $scope.assertionsModelObj.xPathNamespacePrefixesGridOptions = {
      data: 'assertionsModelObj.assertion.properties.namespacePrefixes',
      enableRowHeaderSelection: false, multiSelect: false, enableGridMenu: true, enableColumnMenus: false,
      rowHeight: 20, enableHorizontalScrollbar: uiGridConstants.scrollbars.NEVER,
      columnDefs: [
        {
          name: 'prefix', width: 65, minWidth: 65, headerTooltip: 'Double click to edit',
          sort: { direction: uiGridConstants.ASC, priority: 1 }, enableCellEdit: true,
          editableCellTemplate: 'namespacePrefixGridPrefixEditableCellTemplate.html'
        },
        {
          name: 'namespace', headerTooltip: 'Double click to edit', enableCellEdit: true,
          editableCellTemplate: 'namespacePrefixGridNamespaceEditableCellTemplate.html'
        }
      ],
      gridMenuCustomItems: [
        { title: 'Create', order: 210, action: createNamespacePrefix },
        { title: 'Delete', order: 220, action: removeNamespacePrefix }
      ],
      onRegisterApi: function (gridApi) {
        $scope.assertionsModelObj.xPathNamespacePrefixGridApi = gridApi;
      }
    };
  }
]);
