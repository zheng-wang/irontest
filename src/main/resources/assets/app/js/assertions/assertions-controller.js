'use strict';

angular.module('service-testing-tool').controller('AssertionsController', ['$scope', 'Assertions',
  '$stateParams', 'uiGridConstants', '$timeout',
  function($scope, Assertions, $stateParams, uiGridConstants, $timeout) {
    //  use this to avoid conflict with parent scope
    $scope.assertionsModelObj = {
      showAssertionDetails: false
    };

    var timer;

    $scope.assertionsModelObj.gridOptions = {
      columnDefs: [
        {
          name: 'name', width: 250, minWidth: 250,
          sort: {
            direction: uiGridConstants.ASC,
            priority: 1
          },
          cellTemplate: 'assertionGridNameCellTemplate.html'
        },
        {name: 'type', width: 100, minWidth: 100},
        {name: 'delete', width: 100, minWidth: 100, enableSorting: false,
          cellTemplate: 'assertionGridDeleteCellTemplate.html'
        }
      ],
      onRegisterApi: function (gridApi) {
        $scope.assertionsModelObj.gridApi = gridApi;
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
        $scope.assertionsModelObj.gridOptions.data.push($scope.assertionsModelObj.assertion);
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
          //  re-sort the rows
          $scope.assertionsModelObj.gridApi.core.notifyDataChange(uiGridConstants.dataChange.EDIT);
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

    $scope.assertionsModelObj.edit = function(assertion) {
      $scope.assertionsModelObj.assertion = assertion;
      $scope.assertionsModelObj.showAssertionDetails = true;
    };
  }
]);
