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

    $scope.invoke = function(teststep) {
      var testrun = {
        teststepId: $scope.$parent.teststep.id,
        request: $scope.$parent.teststep.request
      };

      var testrunRes = new Testruns(testrun);
      testrunRes.$save(function(response) {
        var data = response.data;
        $scope.numberOfRowsModified = data.numberOfRowsModified;
        if (data.numberOfRowsModified === -1) {
          $scope.responseOptions.data = data.resultSet;
          $scope.responseOptions.columnDefs = [ ];
          if (data.resultSet.length > 0) {
            var row = data.resultSet[0];
            for (var key in row) {
              $scope.responseOptions.columnDefs.push({
                field: key,
                menuItems: [
                  {
                    title: 'Create An Assertion', icon: 'ui-grid-icon-info-circled',
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
      }, function(response) {
        var errorMessage = null;
        var errorDetails = null;

        if (!response.data) {
          errorMessage = 'Connection refused.';
          errorDetails = 'Unable to talk to the server. Please contact the system administrator.';
        } else {
          errorMessage = response.data.message;
          errorDetails = response.data.details;
        }

        var modalInstance = $uibModal.open({
          templateUrl: '/ui/views/common/error-message-modal.html',
          controller: 'ErrorMessageModalController',
          size: 'md',
          backdrop: 'static',
          windowClass: 'error-message-modal',
          resolve: {
            errorMessage: function () {
              return errorMessage;
            },
            errorDetails: function () {
              return errorDetails;
            }
          }
        });
      });
    };
  }
]);
