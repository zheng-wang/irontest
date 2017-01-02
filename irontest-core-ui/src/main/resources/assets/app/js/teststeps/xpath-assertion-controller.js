'use strict';

//  NOTICE:
//    The $scope here prototypically inherits from the $scope of AssertionsController.
//    ng-include also creates a scope.
angular.module('irontest').controller('XPathAssertionController', ['$scope', 'uiGridConstants', 'IronTestUtils',
  function($scope, uiGridConstants, IronTestUtils) {
    $scope.init = function() {
      if (!$scope.assertionsModelObj.assertion.otherProperties) {
        $scope.assertionsModelObj.assertion.otherProperties = {
          namespacePrefixes: []
        };
      }
    };

    var createNamespacePrefix = function(gridMenuEvent) {
      $scope.assertionsModelObj.assertion.otherProperties.namespacePrefixes.push(
        { prefix: 'ns1', namespace: 'http://com.mycompany/service1' }
      );
      $scope.update(true, $scope.reselectCurrentAssertionInGrid);
    };

    var removeNamespacePrefix = function(gridMenuEvent) {
      var selectedRows = $scope.assertionsModelObj.xPathNamespacePrefixGridApi.selection.getSelectedRows();
      var namespacePrefixes = $scope.assertionsModelObj.assertion.otherProperties.namespacePrefixes;
      for (var i = 0; i < selectedRows.length; i += 1) {
        IronTestUtils.deleteArrayElementByProperty(namespacePrefixes, '$$hashKey', selectedRows[i].$$hashKey);
      }
      $scope.update(true, $scope.reselectCurrentAssertionInGrid);
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
  }
]);