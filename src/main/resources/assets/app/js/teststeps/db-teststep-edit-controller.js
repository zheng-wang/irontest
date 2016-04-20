'use strict';

//  NOTICE:
//    The $scope here prototypically inherits from the $scope of teststeps-controller.js.
//    ng-include also creates a scope.
angular.module('iron-test').controller('DBTeststepEditController', ['$scope', 'Testruns', '$state', '$uibModal',
  function($scope, Testruns, $state, $uibModal) {
    //  -1 when the request is a SQL select statement; > -1 when request is a SQL insert/update/delete statement.
    $scope.numberOfRowsModified = -1;

    $scope.responseOptions = {
      enableFiltering: true,
      columnDefs: [ ]
    };

    $scope.createDSFieldContainAssertion = function(fieldName) {
      $scope.$broadcast('createDSFieldContainAssertion', fieldName);
    };

    $scope.evaluateDataSet = function() {
      $scope.$broadcast('evaluateDataSet', $scope.responseOptions.data);
    };

    $scope.selectManagedEndpoint = function() {
      var modalInstance = $uibModal.open({
        templateUrl: '/ui/views/endpoints/list-modal.html',
        controller: 'EndpointsModalController',
        size: 'lg',
        resolve: {
          endpointType: function () {
            return 'DB';
          }
        }
      });

      modalInstance.result.then(function (selectedEndpoint) {
        $scope.$parent.teststep.endpoint = selectedEndpoint;
        $scope.autoSave(true);
      }, function () {
        //  Modal dismissed
      });
    };

    $scope.invoke = function(teststep) {
      var testrun = {
        teststepId: $scope.$parent.teststep.id,
        request: $scope.$parent.teststep.request
      };

      var testrunRes = new Testruns(testrun);
      testrunRes.$save(function(returnTestrun) {
        var response = returnTestrun.response;
        $scope.numberOfRowsModified = response.numberOfRowsModified;
        if (response.numberOfRowsModified === -1) {
          $scope.responseOptions.data = response.resultSet;
          $scope.responseOptions.columnDefs = [ ];
          if (response.resultSet.length > 0) {
            var row = response.resultSet[0];
            for (var key in row) {
              $scope.responseOptions.columnDefs.push({
                field: key,
                menuItems: [
                  {
                    title: 'Create An Assertion',
                    icon: 'ui-grid-icon-info-circled',
                    context: $scope,
                    action: function() {
                      this.context.createDSFieldContainAssertion(this.context.col.colDef.field);
                    }
                  }
                ]
              });
            }
          }
        }
      }, function(error) {
        alert('Error');
      });
    };
  }
]);
