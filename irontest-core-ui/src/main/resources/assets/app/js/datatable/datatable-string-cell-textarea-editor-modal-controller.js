'use strict';

angular.module('irontest').controller('DataTableStringCellTextareaEditorModalController', ['$scope', 'rowEntity',
    'columnName',
  function($scope, rowEntity, columnName) {
    $scope.rowEntity = rowEntity;
    $scope.columnName = columnName;
  }
]);
