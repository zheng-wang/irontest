'use strict';

//  if unspecified, all grid config is for the assertions grid
angular.module('service-testing-tool').controller('AssertionsController', ['$scope', 'Assertions',
    '$stateParams', 'uiGridConstants', 'uiGridEditConstants', '$timeout', 'STTUtils', '$http',
  function($scope, Assertions, $stateParams, uiGridConstants, uiGridEditConstants, $timeout, STTUtils, $http) {
    //  use assertionsModelObj for all variables in the scope, to avoid conflict with parent scope
    $scope.assertionsModelObj = {};

    $scope.assertionsModelObj.tempData = {};

    var timer;

    $scope.assertionsModelObj.gridOptions = {
      enableRowHeaderSelection: false, multiSelect: false, noUnselect: true,
      columnDefs: [
        {
          name: 'name', width: 250, minWidth: 250, headerTooltip: 'Double click to edit',
          sort: { direction: uiGridConstants.ASC, priority: 1 },
          enableCellEdit: true,
          editableCellTemplate: 'assertionGridNameEditableCellTemplate.html'
        },
        {name: 'type', width: 80, minWidth: 80, enableCellEdit: false},
        {name: 'delete', width: 60, minWidth: 60, enableSorting: false, enableCellEdit: false,
          cellTemplate: 'assertionGridDeleteCellTemplate.html'
        }
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

    $scope.assertionsModelObj.remove = function(assertion) {
      var assertionId = assertion.id;
      assertion.$remove({
        testcaseId: $stateParams.testcaseId,
        teststepId: $stateParams.teststepId
      }, function(response) {
        //  delete the assertion row from the grid
        var gridData = $scope.assertionsModelObj.gridOptions.data;
        var indexOfRowToBeDeleted = STTUtils.indexOfArrayElementByProperty(gridData, 'id', assertionId);
        gridData.splice(indexOfRowToBeDeleted, 1);

        //  if deleted assertion is the one currently selected, set the current assertion to null
        if ($scope.assertionsModelObj.assertion && $scope.assertionsModelObj.assertion.id === assertionId) {
          $scope.assertionsModelObj.assertion = null;
        }
      }, function(error) {
        alert('Error');
      });
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

    $scope.assertionsModelObj.xPathNamespacePrefixesGridOptions = {
      data: 'assertionsModelObj.assertion.properties.namespacePrefixes',
      enableRowHeaderSelection: false, multiSelect: false,
      enableGridMenu: true, enableColumnMenus: false,
      columnDefs: [
        {
          name: 'prefix', width: 70, minWidth: 70, headerTooltip: 'Double click to edit',
          sort: { direction: uiGridConstants.ASC, priority: 1 }, enableCellEdit: true,
          editableCellTemplate: 'namespacePrefixGridPrefixEditableCellTemplate.html'
        },
        {
          name: 'namespace', width: 310, minWidth: 310, headerTooltip: 'Double click to edit',
          enableCellEdit: true, editableCellTemplate: 'namespacePrefixGridNamespaceEditableCellTemplate.html'
        }
      ],
      gridMenuCustomItems: [
        {
          title: 'Create', order: 210,
          action: function ($event) {
            $scope.assertionsModelObj.assertion.properties.namespacePrefixes.push(
              { prefix: 'ns1', namespace: 'http://com.mycompany/service1' }
            );
            assertionUpdateInBackground();
          }
        },
        {
          title: 'Delete', order: 220,
          action: function ($event) {
            var selectedRows = $scope.assertionsModelObj.xPathNamespacePrefixGridApi.selection.getSelectedRows();
            var namespacePrefixes = $scope.assertionsModelObj.assertion.properties.namespacePrefixes;
            for (var i = 0; i < selectedRows.length; i += 1) {
              var indexOfRowToBeDeleted = STTUtils.indexOfArrayElementByProperty(
                namespacePrefixes, '$$hashKey', selectedRows[i].$$hashKey);
              namespacePrefixes.splice(indexOfRowToBeDeleted, 1);
            }
            assertionUpdateInBackground();
          }
        }
      ],
      onRegisterApi: function (gridApi) {
        $scope.assertionsModelObj.xPathNamespacePrefixGridApi = gridApi;
      }
    };
  }
]);
