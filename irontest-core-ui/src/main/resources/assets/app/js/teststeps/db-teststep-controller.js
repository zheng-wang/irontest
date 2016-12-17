'use strict';

//  NOTICE:
//    The $scope here prototypically inherits from the $scope of teststeps-controller.js.
//    ng-include also creates a scope.
angular.module('irontest').controller('DBTeststepController', ['$scope', 'Teststeps', 'IronTestUtils', '$timeout',
    '$http',
  function($scope, Teststeps, IronTestUtils, $timeout, $http) {
    var timer;

    $scope.responseOptions = {
      enableFiltering: true,
      columnDefs: [ ]
    };

    $scope.createDSFieldContainAssertion = function(fieldName) {
      $scope.$broadcast('createDSFieldContainAssertion', fieldName);
    };

    /*$scope.evaluateDataSet = function() {
      $scope.$broadcast('evaluateDataSet', $scope.responseOptions.data);
    };*/

    var clearPreviousRunStatus = function() {
      if (timer) $timeout.cancel(timer);
      $scope.steprun = {};
    };

    $scope.invoke = function() {
      clearPreviousRunStatus();

      //  exclude the result property from the assertion, as the property does not exist in server side Assertion class
      $scope.teststep.assertions.forEach(function(assertion) {
        delete assertion.result;
      });

      var teststep = new Teststeps($scope.teststep);
      $scope.steprun.status = 'ongoing';
      teststep.$run(function(response) {
        $scope.steprun.response = response;
        $scope.steprun.status = 'finished';
        timer = $timeout(function() {
          $scope.steprun.status = null;
        }, 15000);

        if (response.rows) {    //  the request is a select statement
          $scope.responseOptions.data = response.rows;
          $scope.responseOptions.columnDefs = [ ];
          if (response.rows.length > 0) {
            var row = response.rows[0];
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
        $scope.steprun.status = 'failed';
        IronTestUtils.openErrorHTTPResponseModal(response);
      });
    };

    $scope.verifyJSONPathAssertion = function() {
      var url = 'api/jsonservice/verifyassertion';
      var assertionVerification = {
        input: $scope.steprun.response.rows,
        assertion: {
          "id": 10000,
          "teststepId": 10000,
          "name": "abc",
          "type": "JSONPath",
          "otherProperties": {
          	"jsonPath": "$.length()",
            "expectedValue": 0
          }}
      };
      $http
        .post(url, assertionVerification)
        .then(function successCallback(response) {
          //  TODO
        }, function errorCallback(response) {
          IronTestUtils.openErrorHTTPResponseModal(response);
        });
    };
  }
]);
