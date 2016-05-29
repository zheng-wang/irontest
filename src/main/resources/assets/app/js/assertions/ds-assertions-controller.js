'use strict';

angular.module('iron-test').controller('DSAssertionsController', ['$scope', 'Assertions',
  '$stateParams', 'uiGridConstants', '$timeout', 'IronTestUtils', '_',
  function($scope, Assertions, $stateParams, uiGridConstants, $timeout, IronTestUtils, _) {
    //  use assertionsModelObj for all variables in the scope, to avoid conflict with parent scope
    $scope.assertionsModelObj = {};

    var timer;

    $scope.assertionsModelObj.gridOptions = {
      columnDefs: [
        {
          name: 'name', displayName: 'Name', width: 250, minWidth: 250,
          sort: {
            direction: uiGridConstants.ASC,
            priority: 1
          },
          editableCellTemplate: 'assertionGridNameEditableCellTemplate.html'
        },
        {name: 'otherProperties.field', displayName: 'Field', width: 100, minWidth: 100, enableCellEdit: false},
        {name: 'otherProperties.operator', displayName: 'Operator', width: 100, minWidth: 100, enableCellEdit: false},
        {
          name: 'otherProperties.value', displayName: 'Value', width: 200, minWidth: 200,
          editableCellTemplate: 'assertionGridValueEditableCellTemplate.html'
        },
        {name: 'delete', width: 100, minWidth: 100, enableSorting: false, enableCellEdit: false,
          cellTemplate: 'assertionGridDeleteCellTemplate.html'
        },
        {name: 'result', displayName: 'Result', width: 100, minWidth: 100, enableCellEdit: false}
      ]
    };

    $scope.assertionsModelObj.findAll = function() {
      Assertions.query(
          {
            testcaseId: $stateParams.testcaseId,
            teststepId: $stateParams.teststepId
          }, function(response) {
            $scope.assertionsModelObj.gridOptions.data = response;
          }, function(response) {
            IronTestUtils.openErrorHTTPResponseModal(response);
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
      }, function(response) {
        IronTestUtils.openErrorHTTPResponseModal(response);
      });
    };

    $scope.assertionsModelObj.createDSFieldContainAssertion = function(field) {
      var assertion = new Assertions({
        teststepId: $stateParams.teststepId,
        name: IronTestUtils.getNextNameInSequence($scope.assertionsModelObj.gridOptions.data, 'Field contains value'),
        type: 'DSField',
        otherProperties: {
          field: field,
          operator: 'Contains',
          value: ''
        }
      });
      createAssertion(assertion);
    };

    $scope.assertionsModelObj.update = function(isValid) {
      if (isValid) {
        //  exclude the result property from the assertion, as the property does not exist in server side Assertion class
        var assertionCopy = _.omit($scope.assertionsModelObj.assertion, 'result');
        assertionCopy.$update({
          testcaseId: $stateParams.testcaseId,
          teststepId: $stateParams.teststepId
        }, function(response) {
          $scope.$emit('successfullySaved');
          $scope.assertionsModelObj.assertion = response;
        }, function(response) {
          IronTestUtils.openErrorHTTPResponseModal(response);
        });
      } else {
        $scope.savingStatus.submitted = true;
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
        var indexOfRowToBeDeleted = IronTestUtils.indexOfArrayElementByProperty(gridData, 'id', assertionId);
        gridData.splice(indexOfRowToBeDeleted, 1);

        //  if deleted assertion is the one currently selected, set the current assertion to null
        if ($scope.assertionsModelObj.assertion && $scope.assertionsModelObj.assertion.id === assertionId) {
          $scope.assertionsModelObj.assertion = null;
        }
      }, function(response) {
        IronTestUtils.openErrorHTTPResponseModal(response);
      });
    };

    $scope.$on('createDSFieldContainAssertion', function (event, data) {
      $scope.assertionsModelObj.createDSFieldContainAssertion(data);
    });

    $scope.$on('evaluateDataSet', function (event, data) {
      var assertions = $scope.assertionsModelObj.gridOptions.data;
      for (var i = 0; i < assertions.length; i ++) {
        var assertion = assertions[i];
        var values = _.pluck(data, assertion.otherProperties.field);
        assertion.result = _.contains(values, assertion.otherProperties.value);
      }
    });
  }
]);
