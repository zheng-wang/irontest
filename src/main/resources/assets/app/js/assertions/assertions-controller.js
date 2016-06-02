'use strict';

//  NOTICE:
//    The $scope here prototypically inherits from the $scope of teststeps-controller.js.
//    ng-include also creates a scope.
//    If unspecified, all grid config is for the assertions grid
angular.module('iron-test').controller('AssertionsController', ['$scope',
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

    var selectUpdatedAssertionInGrid = function() {
       //  select the updated assertion in the grid
       var updatedAssertionId = $scope.assertionsModelObj.assertion.id;
       var updatedAssertion = $scope.teststep.assertions.find(
           function(asrt) {
             return asrt.id === updatedAssertionId;
           }
       );
       $timeout(function() {    //  a trick for newly loaded grid data
         $scope.assertionsModelObj.gridApi.selection.selectRow(updatedAssertion);
       });
    };

    $scope.assertionsModelObj.autoSave = function(isValid) {
      $scope.autoSave(isValid, selectUpdatedAssertionInGrid);
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
        var newlyCreatedAssertion = $scope.teststep.assertions.find(
            function(asrt) {
              return asrt.name === name;
            }
        );
        $timeout(function() {    //  a trick for newly loaded grid data
          $scope.assertionsModelObj.gridApi.selection.selectRow(newlyCreatedAssertion);
        });
      };
      $scope.update(true, selectNewlyCreatedAssertionInGrid);
    };

    var createNamespacePrefix = function(gridMenuEvent) {
      $scope.assertionsModelObj.assertion.otherProperties.namespacePrefixes.push(
        { prefix: 'ns1', namespace: 'http://com.mycompany/service1' }
      );
      $scope.update(true, selectUpdatedAssertionInGrid);
    };

    var removeNamespacePrefix = function(gridMenuEvent) {
      var selectedRows = $scope.assertionsModelObj.xPathNamespacePrefixGridApi.selection.getSelectedRows();
      var namespacePrefixes = $scope.assertionsModelObj.assertion.otherProperties.namespacePrefixes;
      for (var i = 0; i < selectedRows.length; i += 1) {
        IronTestUtils.deleteArrayElementByProperty(namespacePrefixes, '$$hashKey', selectedRows[i].$$hashKey);
      }
      $scope.update(true, selectUpdatedAssertionInGrid);
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
        input: $scope.$parent.tempData.soapResponse,
        assertion: assertion
      };
      $http
        .post(url, assertionVerification)
        .then(function successCallback(response) {
          var data = response.data;
          $scope.assertionsModelObj.assertionVerificationResult = data;

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
