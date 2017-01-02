'use strict';

//  NOTICE:
//    The $scope here prototypically inherits from the $scope of TeststepsController.
//    ng-include also creates a scope.
angular.module('irontest').controller('DBTeststepController', ['$scope', 'Teststeps', 'IronTestUtils', '$timeout',
    '$http',
  function($scope, Teststeps, IronTestUtils, $timeout, $http) {
    var timer;

    $scope.responseOptions = {
      enableFiltering: true,
      columnDefs: [ ],
      onRegisterApi: function (gridApi) {
        $scope.invocationResultAreaLoadedCallback();
      }
    };

    var clearPreviousRunStatus = function() {
      if (timer) $timeout.cancel(timer);
      var invocationResultArea = document.getElementById('invocationResultArea');
      if (invocationResultArea) {
        var elementHeight = invocationResultArea.offsetHeight;
        $scope.$broadcast('elementRemovedFromColumn', { elementHeight: elementHeight });
      }
      $scope.steprun = {};
    };

    $scope.invoke = function() {
      clearPreviousRunStatus();

      var teststep = new Teststeps($scope.teststep);
      $scope.steprun.status = 'ongoing';
      teststep.$run(function(response) {
        $scope.steprun.status = 'finished';
        timer = $timeout(function() {
          $scope.steprun.status = null;
        }, 15000);

        if (response.rowsJSON) {    //  the request is a select statement, so display result set
          $scope.steprun.response = response.rowsJSON;
          $scope.steprun.isQueryResponse = true;
          $scope.responseOptions.columnDefs = [];
          for (var i = 0; i < response.columnNames.length; i++) {
            $scope.responseOptions.columnDefs.push({
              field: response.columnNames[i]
            });
          }
          $scope.responseOptions.data = response.rowsJSON;
        } else {
          $scope.steprun.response = response.statementExecutionResults;
        }
      }, function(response) {
        $scope.steprun.status = 'failed';
        IronTestUtils.openErrorHTTPResponseModal(response);
      });
    };

    $scope.invocationResultAreaLoadedCallback = function() {
      $timeout(function() {
        var elementHeight = document.getElementById('invocationResultArea').offsetHeight;
        $scope.$broadcast('elementInsertedIntoColumn', { elementHeight: elementHeight });
      });
    };
  }
]);
