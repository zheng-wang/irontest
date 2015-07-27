'use strict';

angular.module('service-testing-tool').controller('AssertionsController', ['$scope', 'Assertions',
    '$stateParams', 'uiGridConstants', 'uiGridEditConstants', '$timeout', 'STTUtils', '$http',
  function($scope, Assertions, $stateParams, uiGridConstants, uiGridEditConstants, $timeout, STTUtils, $http) {
    //  use assertionsModelObj for all variables in the scope, to avoid conflict with parent scope
    $scope.assertionsModelObj = {};

    var timer;

    $scope.assertionsModelObj.gridOptions = {
      enableRowHeaderSelection: false,
      multiSelect: false,
      noUnselect: true,
      columnDefs: [
        {
          name: 'name', displayName: 'Name (double click to edit)', width: 250, minWidth: 250,
          sort: {
            direction: uiGridConstants.ASC,
            priority: 1
          },
          enableCellEdit: true,
          editableCellTemplate: 'assertionGridNameEditableCellTemplate.html'
        },
        {name: 'type', width: 100, minWidth: 100, enableCellEdit: false},
        {name: 'delete', width: 100, minWidth: 100, enableSorting: false, enableCellEdit: false,
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

    //  highlight the current assertion in the grid
    var selectCurrentAssertionInGrid = function() {
      var gridData = $scope.assertionsModelObj.gridOptions.data;
      var indexOfGridDataRow = STTUtils.indexOfArrayElementByProperty(
        gridData, 'id', $scope.assertionsModelObj.assertion.id);
      $timeout(function() {    //  a trick for newly loaded grid data
        $scope.assertionsModelObj.gridApi.selection.selectRow(gridData[indexOfGridDataRow]);
      });
    };

    $scope.$on(uiGridEditConstants.events.END_CELL_EDIT,
      function () {
        //  re-sort the assertion grid rows
        $scope.assertionsModelObj.gridApi.core.notifyDataChange(uiGridConstants.dataChange.EDIT);

        //  ensure the selection (highlight) is not lost
        selectCurrentAssertionInGrid();
      }
    );

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

        //  add the new assertion to the grid data
        $scope.assertionsModelObj.gridOptions.data.push(response);

        selectCurrentAssertionInGrid();
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
         xPath: 'true',
         expectedValue: 'true'
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

    //  evaluate xpath against the target xml
    $scope.assertionsModelObj.evaluateXPath = function(xpath, input) {
      var url = 'api/evaluator';
      $http
        .post(url, {
          type: 'XPath',
          expression: xpath,
          input: input
        })
        .success(function(data, status) {
          $scope.assertionsModelObj.assertion.properties.actualValue = data.result;
        })
        .error(function(data, status) {
          alert('Error');
        });
    };
  }
]);
