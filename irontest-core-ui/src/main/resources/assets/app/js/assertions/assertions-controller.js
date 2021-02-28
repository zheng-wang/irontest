'use strict';

//  NOTICE:
//    The $scope here prototypically inherits from the $scope of the specific test step controller,
//      such as SOAPTeststepActionController or DBTeststepController.
//    ng-include also creates a scope.
//    If unspecified, all grid config is for the assertions grid
angular.module('irontest').controller('AssertionsController', ['$scope', '$rootScope', 'uiGridConstants',
    'IronTestUtils', '$http',
  function($scope, $rootScope, uiGridConstants, IronTestUtils, $http) {
    //  use assertionsModelObj for all variables in the scope, to avoid conflict with parent scope
    $scope.assertionsModelObj = {
      assertionVerificationResults: {}
    };

    $scope.$watch('$parent.steprun.response', function() {
      $scope.assertionsModelObj.assertionVerificationResults = {};
    });

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

    $scope.$watch('teststep.assertions', function() {
      $scope.assertionsModelObj.gridOptions.data = $scope.teststep.assertions;
    });

    $scope.assertionsModelObj.gridOptions = {
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
          shown: function() {
            return !$rootScope.appStatus.isForbidden() &&
              $scope.assertionsModelObj.gridApi.selection.getSelectedRows().length === 1;
          }
        }
      ],
      onRegisterApi: function (gridApi) {
        $scope.bottomPaneLoadedCallback();
        $scope.assertionsModelObj.gridApi = gridApi;
        gridApi.selection.on.rowSelectionChanged($scope, function(row) {
          $scope.assertionsModelObj.assertion = row.entity;
        });
        gridApi.edit.on.afterCellEdit($scope, function(rowEntity, colDef, newValue, oldValue){
          if (newValue !== oldValue) {
            $scope.update(true, $scope.assertionsModelObj.reselectCurrentAssertionInGrid);
          }
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

    $scope.assertionsModelObj.clearCurrentAssertionVerificationResult = function() {
      delete $scope.assertionsModelObj.assertionVerificationResults[$scope.assertionsModelObj.assertion.id];
    };

    $scope.assertionsModelObj.autoSave = function(isValid) {
      $scope.assertionsModelObj.clearCurrentAssertionVerificationResult();
      $scope.autoSave(isValid, $scope.assertionsModelObj.reselectCurrentAssertionInGrid);
    };

    $scope.assertionsModelObj.createAssertion = function(type) {
      var name = IronTestUtils.getNextNameInSequence($scope.teststep.assertions, type + ' ');
      var assertion = {
        name: name,
        type: type,
        otherProperties: {}  //  adding this property here to avoid Jackson 'Missing property' error (http://stackoverflow.com/questions/28089484/deserialization-with-jsonsubtypes-for-no-value-missing-property-error)
      };
      $scope.teststep.assertions.push(assertion);
      var selectNewlyCreatedAssertionInGrid = function() {
        selectAssertionInGridByProperty('name', name);
      };
      $scope.update(true, selectNewlyCreatedAssertionInGrid);
    };

    $scope.assertionsModelObj.verifyCurrentAssertion = function() {
      var assertion = $scope.assertionsModelObj.assertion;

      //  resolve assertion input
      var input;
      var apiResponse = $scope.$parent.steprun.response;
      if ($scope.teststep.type === 'SOAP' || $scope.teststep.type === 'HTTP') {
        if (assertion.type === 'StatusCodeEqual') {
          input = apiResponse.statusCode;
        } else {
          input = apiResponse.httpBody;
        }
      } else if ($scope.teststep.type === 'JMS') {
        input = apiResponse.body;
      } else if ($scope.teststep.type === 'MQ') {
        if (assertion.type === 'HasAnMQRFH2FolderEqualToXml') {
          input = apiResponse.mqrfh2Header;
        } else {
          input = apiResponse.bodyAsText;
        }
      } else {
        input = apiResponse;
      }

      var url = 'api/assertions/' + assertion.id + '/verify';
      var assertionVerificationRequest = { input: input, assertion: assertion };
      $http
        .post(url, assertionVerificationRequest)
        .then(function successCallback(response) {
          var data = response.data;
          $scope.assertionsModelObj.assertionVerificationResults[assertion.id] = data;
        }, function errorCallback(response) {
          IronTestUtils.openErrorHTTPResponseModal(response);
        });
    };
  }
]);
