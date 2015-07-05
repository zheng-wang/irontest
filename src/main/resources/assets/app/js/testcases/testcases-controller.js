'use strict';

angular.module('service-testing-tool').controller('TestcasesController', ['$scope', 'Testcases', '$stateParams', '$state', 'uiGridConstants', '$timeout',
  function($scope, Testcases, $stateParams, $state, uiGridConstants, $timeout) {
    $scope.testcase = {};
    $scope.saveSuccessful = null;

    $scope.create_update = function(isValid) {
      if (isValid) {
        if (this.testcase.id) {
          var testcase = this.testcase;
          testcase.$update(function(response) {
            $scope.saveSuccessful = true;
            $scope.testcase = response;
          }, function(error) {
            $scope.savingErrorMessage = error.data.message;
            $scope.saveSuccessful = false;
          });
        } else {
          var testcase = new Testcases(this.testcase);
          testcase.$save(function(response) {
            $state.go('testcase_edit', {testcaseId: response.id});
          });
        }
      } else {
        $scope.submitted = true;
      }
    };

    $scope.remove = function(testcase) {
      testcase.$remove(function(response) {
          $state.go($state.current, {}, {reload: true});
      });
    };

    $scope.find = function() {
      $scope.columnDefs = [
        {
          name: 'name', width: 200, minWidth: 100,
          sort: {
            direction: uiGridConstants.ASC,
            priority: 1
          }
        },
        {name: 'description', width: 585, minWidth: 300}
      ];

      Testcases.query(function(testcases) {
        $scope.testcases = testcases;
      });
    };

    $scope.findOne = function() {
      if ($stateParams.testcaseId) {
        Testcases.get({
          testcaseId: $stateParams.testcaseId
        }, function(testcase) {
          $scope.testcase = testcase;
        });
      };
    }
  }
]);
