'use strict';

//  NOTICE:
//    The $scope here prototypically inherits from the $scope of the specific test step controller,
//      such as SOAPTeststepsController or DBTeststepController.
//    ng-include also creates a scope.
//    If unspecified, all grid config is for the assertions grid
angular.module('irontest').controller('AssertionsController', ['$scope', 'uiGridConstants', 'IronTestUtils', '$http',
    '$timeout',
  function($scope, uiGridConstants, IronTestUtils, $http, $timeout) {
    //  use assertionsModelObj for all variables in the scope, to avoid conflict with parent scope
    $scope.assertionsModelObj = {};

    //  remove currently selected assertion
    var removeCurrentAssertion = function() {
      var currentAssertion = $scope.assertionsModelObj.assertion;
      if (currentAssertion) {
        //  set current assertion to null
        $scope.assertionsModelObj.assertion = null;
        IronTestUtils.deleteArrayElementByProperty($scope.teststep.assertions, 'id', currentAssertion.id);
        $scope.update(true);
      }
    };

    $scope.assertionsModelObj.gridOptions = {
      data: 'teststep.assertions',
      enableRowHeaderSelection: false, multiSelect: false, noUnselect: true,
      enableGridMenu: true, gridMenuShowHideColumns: false, enableColumnMenus: false,
      columnDefs: [
        {
          name: 'name', headerTooltip: 'Double click to edit',
          sort: { direction: uiGridConstants.ASC, priority: 1 }, cellTemplate: 'assertionGridNameCellTemplate.html',
          enableCellEdit: true, editableCellTemplate: 'assertionGridNameEditableCellTemplate.html'
        },
        { name: 'type', width: 80, minWidth: 80, enableCellEdit: false }
      ],
      gridMenuCustomItems: [
        { title: 'Delete', order: 210, action: removeCurrentAssertion,
          shown: function() { return $scope.assertionsModelObj.gridApi.selection.getSelectedRows().length === 1; } }
      ],
      onRegisterApi: function (gridApi) {
        $scope.assertionsAreaLoadedCallback();
        $scope.assertionsModelObj.gridApi = gridApi;
        gridApi.selection.on.rowSelectionChanged($scope, function(row) {
          $scope.assertionsModelObj.assertion = row.entity;
          $scope.assertionsModelObj.assertionVerificationResult = null;
        });
      }
    };

    var selectAssertionInGridByProperty = function(propertyName, propertyValue) {
      var assertions = $scope.teststep.assertions;
      var assertion = assertions.find(
        function(asrt) {
          return asrt[propertyName] === propertyValue;
        }
      );
      var gridApi = $scope.assertionsModelObj.gridApi;
      gridApi.grid.modifyRows(assertions);
      gridApi.selection.selectRow(assertion);
    };

    $scope.assertionsModelObj.reselectCurrentAssertionInGrid = function() {
       var currentAssertionId = $scope.assertionsModelObj.assertion.id;
       selectAssertionInGridByProperty('id', currentAssertionId);
    };

    $scope.assertionsModelObj.autoSave = function(isValid) {
      $scope.autoSave(isValid, $scope.assertionsModelObj.reselectCurrentAssertionInGrid);
    };

    $scope.assertionsModelObj.createAssertion = function(type) {
      var name = IronTestUtils.getNextNameInSequence($scope.teststep.assertions, type);
      var assertion = {
        name: name,
        type: type,
        otherProperties: null  //  this is to avoid Jackson 'Missing property' error (http://stackoverflow.com/questions/28089484/deserialization-with-jsonsubtypes-for-no-value-missing-property-error)
      };
      $scope.teststep.assertions.push(assertion);
      var selectNewlyCreatedAssertionInGrid = function() {
        selectAssertionInGridByProperty('name', name);
      };
      $scope.update(true, selectNewlyCreatedAssertionInGrid);
    };

    $scope.assertionsModelObj.verifyCurrentAssertion = function() {
      var assertion = $scope.assertionsModelObj.assertion;
      var url = 'api/jsonservice/verifyassertion';
      var assertionVerification = {
        input: $scope.$parent.steprun.response,
        assertion: assertion
      };
      $http
        .post(url, assertionVerification)
        .then(function successCallback(response) {
          var data = response.data;
          $scope.assertionsModelObj.assertionVerificationResult = data;
          $scope.assertionsModelObj.assertionVerificationResult.assertionId = assertion.id;
        }, function errorCallback(response) {
          IronTestUtils.openErrorHTTPResponseModal(response);
        });
    };
  }
]);
