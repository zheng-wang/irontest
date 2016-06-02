'use strict';

angular.module('iron-test').controller('DSAssertionsController', ['$scope', '$stateParams', 'uiGridConstants',
    'IronTestUtils', '_',
  function($scope, $stateParams, uiGridConstants, IronTestUtils, _) {
    //  use assertionsModelObj for all variables in the scope, to avoid conflict with parent scope
    $scope.assertionsModelObj = {};

    $scope.assertionsModelObj.gridOptions = {
      data: 'teststep.assertions',
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

    $scope.assertionsModelObj.createDSFieldContainAssertion = function(field) {
      var type = 'DSField';
      var name = IronTestUtils.getNextNameInSequence($scope.teststep.assertions, 'Field contains value');
      var assertion = {
        name: name,
        type: type,
        otherProperties: {
          field: field,
          operator: 'Contains',
          value: ''
        }
      };
      $scope.teststep.assertions.push(assertion);
      //  exclude the result property from the assertion, as the property does not exist in server side Assertion class
      $scope.teststep.assertions.forEach(function(assertion) {
        delete assertion.result;
      });
      var selectNewlyCreatedAssertion = function() {
        selectAssertionByProperty('name', name);
      };
      $scope.update(true, selectNewlyCreatedAssertion);
    };

    var selectAssertionByProperty = function(propertyName, propertyValue) {
      var assertion = $scope.teststep.assertions.find(
        function(asrt) {
          return asrt[propertyName] === propertyValue;
        }
      );
      $scope.assertionsModelObj.assertion = assertion;
    };

    var reselectCurrentAssertionInGrid = function() {
       var currentAssertionId = $scope.assertionsModelObj.assertion.id;
       selectAssertionByProperty('id', currentAssertionId);
    };

    $scope.assertionsModelObj.autoSave = function(isValid) {
      //  exclude the result property from the assertion, as the property does not exist in server side Assertion class
      $scope.teststep.assertions.forEach(function(assertion) {
        delete assertion.result;
      });
      $scope.autoSave(isValid, reselectCurrentAssertionInGrid);
    };

    $scope.assertionsModelObj.remove = function(assertion) {
      var assertionId = assertion.id;
      IronTestUtils.deleteArrayElementByProperty($scope.teststep.assertions, 'id', assertionId);
      //  exclude the result property from the assertion, as the property does not exist in server side Assertion class
      $scope.teststep.assertions.forEach(function(assertion) {
        delete assertion.result;
      });
      $scope.update(true, function() {
        //  if deleted assertion is the one currently selected, set the current assertion to null
        if ($scope.assertionsModelObj.assertion && $scope.assertionsModelObj.assertion.id === assertionId) {
          $scope.assertionsModelObj.assertion = null;
        }
      });
    };

    $scope.$on('createDSFieldContainAssertion', function (event, data) {
      $scope.assertionsModelObj.createDSFieldContainAssertion(data);
    });

    $scope.$on('evaluateDataSet', function (event, data) {
      var assertions = $scope.teststep.assertions;
      for (var i = 0; i < assertions.length; i ++) {
        var assertion = assertions[i];
        var values = _.pluck(data, assertion.otherProperties.field);
        assertion.result = _.contains(values, assertion.otherProperties.value);
      }
    });
  }
]);
