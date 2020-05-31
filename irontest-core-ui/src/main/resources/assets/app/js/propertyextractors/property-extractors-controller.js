'use strict';

//  NOTICE:
//    The $scope here prototypically inherits from the $scope of the specific test step controller.
//    ng-include also creates a scope.
angular.module('irontest').controller('PropertyExtractorsController', ['$scope', 'IronTestUtils', 'PropertyExtractors',
    '$stateParams', 'uiGridConstants', '$timeout', '$rootScope',
  function($scope, IronTestUtils, PropertyExtractors, $stateParams, uiGridConstants, $timeout, $rootScope) {
    var clearCurrentPropertyExtractionResult = function() {
      delete $scope.propertyExtractionResult;
    };

    var removeSelectedPropertyExtractor = function() {
      var propertyExtractor = $scope.propertyExtractor;
      propertyExtractor.$remove(function(response) {
        delete $scope.propertyExtractor;
        IronTestUtils.deleteArrayElementByProperty($scope.propertyExtractors, 'id', propertyExtractor.id);
      }, function(response) {
        IronTestUtils.openErrorHTTPResponseModal(response);
      });
    };

    $scope.propertyExtractorsGridOptions = {
      data: 'propertyExtractors',
      enableRowHeaderSelection: false, multiSelect: false, noUnselect: true,
      enableGridMenu: true, gridMenuShowHideColumns: false, enableColumnMenus: false,
      columnDefs: [
        {
          name: 'propertyName', displayName: 'Property Name', headerTooltip: 'Double click to edit',
          sort: { direction: uiGridConstants.ASC, priority: 1 },
          enableCellEdit: true, editableCellTemplate: 'propertyExtractorsGridPropertyNameEditableCellTemplate.html'
        },
        { name: 'type', displayName: 'Extractor Type', width: 127, minWidth: 80, enableCellEdit: false }
      ],
      gridMenuCustomItems: [
        { title: 'Delete', order: 210, action: removeSelectedPropertyExtractor,
          shown: function() {
            return !$rootScope.appStatus.isForbidden() &&
              $scope.propertyExtractorsGridApi.selection.getSelectedRows().length === 1;
          }
        }
      ],
      onRegisterApi: function (gridApi) {
        $scope.bottomPaneLoadedCallback();
        $scope.propertyExtractorsGridApi = gridApi;
        gridApi.selection.on.rowSelectionChanged($scope, function(row) {
          clearCurrentPropertyExtractionResult();
          $scope.propertyExtractor = row.entity;
        });
        gridApi.edit.on.afterCellEdit($scope, function(rowEntity, colDef, newValue, oldValue){
          if (newValue !== oldValue) {
            $scope.propertyExtractorUpdate();
          }
        });
      }
    };

    var timer;
    $scope.propertyExtractorAutoSave = function() {
      clearCurrentPropertyExtractionResult();
      if (timer) $timeout.cancel(timer);
      timer = $timeout(function() {
        $scope.propertyExtractorUpdate();
      }, 2000);
    };

    $scope.propertyExtractorUpdate = function() {
      if (timer) $timeout.cancel(timer);  //  cancel existing timer if the update function is called directly (to avoid duplicate save)
      $scope.propertyExtractor.$update(function(response) {
        $scope.$emit('successfullySaved');
      }, function(response) {
        IronTestUtils.openErrorHTTPResponseModal(response);
      });
    };

    $scope.findPropertyExtractorsByTeststepId = function() {
      PropertyExtractors.query({ teststepId: $stateParams.teststepId }, function(returnPropertyExtractors) {
        $scope.propertyExtractors = returnPropertyExtractors;
      }, function(response) {
        IronTestUtils.openErrorHTTPResponseModal(response);
      });
    };

    var selectPropertyExtractorInGridByPropertyName = function(propertyName) {
      var propertyExtractors = $scope.propertyExtractors;
      var propertyExtractor = propertyExtractors.find(
        function(propExtr) {
          return propExtr.propertyName === propertyName;
        }
      );
      var gridApi = $scope.propertyExtractorsGridApi;
      gridApi.grid.modifyRows(propertyExtractors);
      gridApi.selection.selectRow(propertyExtractor);
    };

    $scope.createPropertyExtractor = function(type) {
      var propertyName = IronTestUtils.getNextNameInSequence($scope.propertyExtractors, 'Property', 'propertyName');
      var propertyExtractor = new PropertyExtractors({
        propertyName: propertyName,
        type: type,
        otherProperties: {}  //  adding this property here to avoid Jackson 'Missing property' error (http://stackoverflow.com/questions/28089484/deserialization-with-jsonsubtypes-for-no-value-missing-property-error)
      });
      propertyExtractor.$save({ teststepId: $stateParams.teststepId }, function(returnPropertyExtractor) {
        $scope.propertyExtractors.push(propertyExtractor);
        $scope.$emit('successfullySaved');

        //  select newly created property extractor in grid
        selectPropertyExtractorInGridByPropertyName(propertyName);
      }, function(response) {
        IronTestUtils.openErrorHTTPResponseModal(response);
      });
    };

    $scope.extractProperty = function() {
      var propertyExtractor = $scope.propertyExtractor;

      //  resolve property extraction input
      var input;
      var apiResponse = $scope.$parent.steprun.response;
      if ($scope.teststep.type === 'HTTP') {
        if (propertyExtractor.type === 'Cookie') {
          var setCookieHeader = apiResponse.httpHeaders.find(header => header.name === 'Set-Cookie');
          input = setCookieHeader ? setCookieHeader.value : null;
        } else {
          input = apiResponse.httpBody;
        }
      } else {
        input = apiResponse;
      }

      var propertyExtractionRequest = { input: input, propertyExtractor: propertyExtractor };
      PropertyExtractors.extract({ propertyExtractorId: propertyExtractor.id }, propertyExtractionRequest, function(response) {
        $scope.propertyExtractionResult = response;
      }, function(response) {
        IronTestUtils.openErrorHTTPResponseModal(response);
      });
    };

    $scope.$watch('steprun.response', function() {
      clearCurrentPropertyExtractionResult();
    }, true);
  }
]);
