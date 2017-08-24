'use strict';

//  NOTICE:
//    The $scope here prototypically inherits from the $scope of AssertionsController.
//    ng-include also creates a scope.
angular.module('irontest').controller('XPathAssertionController', ['$scope', 'uiGridConstants', 'IronTestUtils',
  function($scope, uiGridConstants, IronTestUtils) {
    var createNamespacePrefix = function(gridMenuEvent) {
      $scope.assertionsModelObj.assertion.otherProperties.namespacePrefixes.push(
        { prefix: 'ns1', namespace: 'http://com.mycompany/service1' }
      );
      $scope.assertionsModelObj.clearCurrentAssertionVerificationResult();
      $scope.update(true, $scope.assertionsModelObj.reselectCurrentAssertionInGrid);
    };

    var removeNamespacePrefix = function(gridMenuEvent) {
      var selectedRow = $scope.assertionsModelObj.xPathNamespacePrefixGridApi.selection.getSelectedRows()[0];
      var namespacePrefixes = $scope.assertionsModelObj.assertion.otherProperties.namespacePrefixes;
      IronTestUtils.deleteArrayElementByProperty(namespacePrefixes, '$$hashKey', selectedRow.$$hashKey);
      $scope.assertionsModelObj.clearCurrentAssertionVerificationResult();
      $scope.update(true, $scope.assertionsModelObj.reselectCurrentAssertionInGrid);
    };

    //  notice that when switching assertionsModelObj.assertion in parent controller, if the newly selected
    //  assertionsModelObj.assertion has null otherProperties, the data of this grid will not update (probably due to
    //  angularjs/ui-grid not being able to detect changes to a null's 'child object').
    $scope.assertionsModelObj.xPathNamespacePrefixesGridOptions = {
      data: 'assertionsModelObj.assertion.otherProperties.namespacePrefixes',
      enableRowHeaderSelection: false, multiSelect: false, enableGridMenu: true, gridMenuShowHideColumns: false,
      enableColumnMenus: false, rowHeight: 20, enableHorizontalScrollbar: uiGridConstants.scrollbars.NEVER,
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
        { title: 'Delete', order: 220, action: removeNamespacePrefix, shown: function() {
          return $scope.assertionsModelObj.xPathNamespacePrefixGridApi.selection.getSelectedRows().length === 1; } }
      ],
      onRegisterApi: function (gridApi) {
        $scope.assertionsModelObj.xPathNamespacePrefixGridApi = gridApi;
      }
    };
  }
]);