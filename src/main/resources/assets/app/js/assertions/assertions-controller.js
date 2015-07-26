'use strict';

angular.module('service-testing-tool').controller('AssertionsController', ['$scope', 'Assertions',
    '$stateParams', 'uiGridConstants', 'uiGridEditConstants', '$timeout',
  function($scope, Assertions, $stateParams, uiGridConstants, uiGridEditConstants, $timeout) {
    //  use this to avoid conflict with parent scope
    $scope.assertionsModelObj = {
      showAssertionDetails: false
    };

    var timer;

    $scope.assertionsModelObj.gridOptions = {
      enableRowHeaderSelection: false,
      multiSelect: false,
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
        gridApi.selection.on.rowSelectionChanged($scope, function(row){
          $scope.assertionsModelObj.assertion = row.entity;
          $scope.assertionsModelObj.showAssertionDetails = true;
        });
      }
    };

    $scope.$on(uiGridEditConstants.events.END_CELL_EDIT,
      function () {
        //  re-sort the assertion grid rows
        $scope.assertionsModelObj.gridApi.core.notifyDataChange(uiGridConstants.dataChange.EDIT);
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

    $scope.assertionsModelObj.createContainsAssertion = function() {
      var assertion = new Assertions({
        teststepId: $stateParams.teststepId,
        name: 'Response contains value',
        type: 'Contains',
        properties: { contains: 'value' }
      });

      assertion.$save({
        testcaseId: $stateParams.testcaseId,
        teststepId: $stateParams.teststepId
      }, function(response) {
        $scope.assertionsModelObj.assertion = response;

        var gridData = $scope.assertionsModelObj.gridOptions.data;
        gridData.push($scope.assertionsModelObj.assertion);
        $timeout(function() {    //  a trick for newly loaded grid data
          $scope.assertionsModelObj.gridApi.selection.selectRow(gridData[gridData.length - 1]);
        });
      }, function(error) {
        alert('Error');
      });

      $scope.assertionsModelObj.showAssertionDetails = true;
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
      assertion.$remove({
        testcaseId: $stateParams.testcaseId,
        teststepId: $stateParams.teststepId
      }, function(response) {
        $scope.assertionsModelObj.findAll();
      }, function(error) {
        alert('Error');
      });
    };
  }
]);
