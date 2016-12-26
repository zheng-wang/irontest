'use strict';

//  NOTICE:
//    The $scope here prototypically inherits from the $scope of teststeps-controller.js.
//    ng-include also creates a scope.
//    If unspecified, all grid config is for the assertions grid
angular.module('irontest').controller('AssertionsController', ['$scope',
    '$stateParams', 'uiGridConstants', 'uiGridEditConstants', 'IronTestUtils', '$http', '$timeout',
  function($scope, $stateParams, uiGridConstants, uiGridEditConstants, IronTestUtils, $http, $timeout) {
    //  use assertionsModelObj for all variables in the scope, to avoid conflict with parent scope
    $scope.assertionsModelObj = {};

    $scope.assertionsModelObj.tempData = {};

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
      enableRowHeaderSelection: false, multiSelect: false, noUnselect: true, enableGridMenu: true,
      enableColumnMenus: false,
      columnDefs: [
        {
          name: 'name', width: 260, minWidth: 260, headerTooltip: 'Double click to edit',
          sort: { direction: uiGridConstants.ASC, priority: 1 }, cellTemplate: 'assertionGridNameCellTemplate.html',
          enableCellEdit: true, editableCellTemplate: 'assertionGridNameEditableCellTemplate.html'
        },
        { name: 'type', width: 80, minWidth: 80, enableCellEdit: false }
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

    var selectAssertionInGridByProperty = function(propertyName, propertyValue) {
      var assertion = $scope.teststep.assertions.find(
        function(asrt) {
          return asrt[propertyName] === propertyValue;
        }
      );
      $timeout(function() {    //  a trick for newly loaded grid data
        $scope.assertionsModelObj.gridApi.selection.selectRow(assertion);
      });
    };

    var reselectCurrentAssertionInGrid = function() {
       var currentAssertionId = $scope.assertionsModelObj.assertion.id;
       selectAssertionInGridByProperty('id', currentAssertionId);
    };

    $scope.assertionsModelObj.autoSave = function(isValid) {
      $scope.autoSave(isValid, reselectCurrentAssertionInGrid);
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

    var createNamespacePrefix = function(gridMenuEvent) {
      $scope.assertionsModelObj.assertion.otherProperties.namespacePrefixes.push(
        { prefix: 'ns1', namespace: 'http://com.mycompany/service1' }
      );
      $scope.update(true, reselectCurrentAssertionInGrid);
    };

    var removeNamespacePrefix = function(gridMenuEvent) {
      var selectedRows = $scope.assertionsModelObj.xPathNamespacePrefixGridApi.selection.getSelectedRows();
      var namespacePrefixes = $scope.assertionsModelObj.assertion.otherProperties.namespacePrefixes;
      for (var i = 0; i < selectedRows.length; i += 1) {
        IronTestUtils.deleteArrayElementByProperty(namespacePrefixes, '$$hashKey', selectedRows[i].$$hashKey);
      }
      $scope.update(true, reselectCurrentAssertionInGrid);
    };

    $scope.assertionsModelObj.xPathNamespacePrefixesGridOptions = {
      data: 'assertionsModelObj.assertion.otherProperties.namespacePrefixes',
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

          if (assertion.type === 'XPath') {
            $scope.assertionsModelObj.tempData.assertionXPathActualValue =
              data.error ? data.error : data.actualValue;
            $scope.assertionsModelObj.tempData.assertionXPathActualValueError = data.error;
          }
        }, function errorCallback(response) {
          IronTestUtils.openErrorHTTPResponseModal(response);
        });
    };
  }
]);
