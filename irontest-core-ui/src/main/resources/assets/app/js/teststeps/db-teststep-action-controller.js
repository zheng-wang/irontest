'use strict';

//  NOTICE:
//    The $scope here prototypically inherits from the $scope of TeststepsActionController.
//    ng-include also creates a scope.
angular.module('irontest').controller('DBTeststepActionController', ['$scope', 'Teststeps', 'IronTestUtils', '$timeout',
    '$http',
  function($scope, Teststeps, IronTestUtils, $timeout, $http) {
    var timer;

    $scope.$watch('teststepParameters.isSQLRequestSingleSelectStatement', function(newValue, oldValue) {
      if (oldValue === true && newValue === false) {
        $scope.$parent.$parent.removeBottomPaneFromColumn();
        $scope.$parent.$parent.showBottomPane = false;
        $scope.$parent.$parent.bottomButtonModel.selectedButton = null;
        $scope.showAssertionsButton = false;
      } else if (oldValue === false && newValue === true) {
        $scope.$parent.$parent.showBottomPane = false;
        $scope.$parent.$parent.bottomButtonModel.selectedButton = null;
        $scope.showAssertionsButton = true;
      } else if (oldValue === true && newValue === true) {    //  on view being newly loaded (by such as refreshing the page)
        $scope.showAssertionsButton = true;
      }
    });

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
      teststep.$run(function(basicTeststepRun) {
        $scope.steprun.status = 'finished';
        timer = $timeout(function() {
          $scope.steprun.status = null;
        }, 15000);

        var response = basicTeststepRun.response;
        if (response.rowsJSON) {    //  the request is a select statement, so display result set
          $scope.steprun.response = response.rowsJSON;
          $scope.steprun.isQueryResponse = true;
          $scope.responseOptions.columnDefs = [];
          for (var i = 0; i < response.columnNames.length; i++) {
            $scope.responseOptions.columnDefs.push({
              name: window.btoa(response.columnNames[i]),
              displayName: response.columnNames[i],
              // determine column min width according to the length of column name
              // assuming each character deserves 8 pixels
              // 30 pixels for displaying grid header menu arrow
              minWidth: response.columnNames[i].length * 8 + 30
            });
          }
          //  in grid data, column names are base64 encoded due to parentheses (seems only right parenthesis) in column name, such as 'count(*)', causing the column data not displayed
          //  refer to https://github.com/angular-ui/ui-grid/issues/5169
          var rows = angular.fromJson(response.rowsJSON);
          var newRows = [];
          for (var i = 0; i < rows.length; i++) {
            var row = rows[i];
            var newRow = {}
            var keys = Object.keys(row);
            for (var j = 0; j < keys.length; j++) {
              newRow[window.btoa(keys[j])] = row[keys[j]];
            }
            newRows[i] = newRow;
          }
          $scope.responseOptions.data = newRows;
        } else {
          $scope.steprun.response = response.statementExecutionResults;
        }
      }, function(error) {
        $scope.steprun.status = 'failed';
        IronTestUtils.openErrorHTTPResponseModal(error);
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
